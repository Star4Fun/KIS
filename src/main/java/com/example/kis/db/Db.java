package com.example.kis.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import java.sql.Connection;
import java.util.Objects;
import org.flywaydb.core.Flyway;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Simple DB bootstrap: HikariCP + Flyway. Reads JDBC settings from env with sensible defaults for
 * local dev.
 *
 * <p>Env vars (override as needed): KIS_DB_URL (default:
 * jdbc:mariadb://localhost:3306/kis?useUnicode=true{@literal &}characterEncoding=utf8) KIS_DB_USER
 * (default: kis_user) KIS_DB_PASS (default: secret)
 */
public final class Db {
  private static final Logger log = LoggerFactory.getLogger(Db.class);
  private static volatile HikariDataSource DS;

  private Db() {}

  public static synchronized void init() {
    if (DS != null) return;

    final String url =
        getenvOr(
            "KIS_DB_URL",
            "jdbc:mariadb://localhost:3306/kis?useUnicode=true&characterEncoding=utf8");
    final String user = getenvOr("KIS_DB_USER", "kis_user");
    final String pass = getenvOr("KIS_DB_PASS", "secret");

    // 1) Migrate schema with Flyway (before opening the pool)
    log.info("Running Flyway migrations on {}", url);
    Flyway.configure()
        .dataSource(url, user, pass)
        .locations("classpath:db/migration")
        .baselineOnMigrate(true) // allows starting on non-empty DBs
        .load()
        .migrate();

    // 2) Configure HikariCP pool
    HikariConfig cfg = new HikariConfig();
    cfg.setJdbcUrl(url);
    cfg.setUsername(user);
    cfg.setPassword(pass);
    cfg.setDriverClassName("org.mariadb.jdbc.Driver");
    cfg.setMaximumPoolSize(10);
    cfg.setMinimumIdle(2);
    cfg.setPoolName("kis-hikari");

    DS = new HikariDataSource(cfg);
    log.info(
        "HikariCP started (poolName={}, maxPoolSize={})",
        cfg.getPoolName(),
        cfg.getMaximumPoolSize());

    // 3) Quick health check
    try (Connection c = DS.getConnection()) {
      log.info("DB connection OK (catalog: {})", c.getCatalog());
    } catch (Exception e) {
      log.error("DB connection failed", e);
      throw new IllegalStateException("Cannot connect to DB", e);
    }
  }

  public static HikariDataSource dataSource() {
    if (DS == null) {
      throw new IllegalStateException("Db.init() not called yet.");
    }
    return DS;
  }

  public static synchronized void shutdown() {
    if (DS != null) {
      log.info("Shutting down HikariCP.");
      DS.close();
      DS = null;
    }
  }

  private static String getenvOr(String key, String def) {
    String v = System.getenv(key);
    return (v == null || v.isBlank()) ? Objects.requireNonNull(def) : v;
  }
}
