package com.claro.postventa.proxy.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.claro.postventa.proxy.to.ProxyRoute;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import com.netflix.config.sources.JDBCConfigurationSource;

@Singleton
public class ProxyConfigurationDAO {
	
	private static Logger logger = LoggerFactory.getLogger(ProxyConfigurationDAO.class);
	
	private DataSource datasource;
	
	@Inject
	public ProxyConfigurationDAO(DataSource datasource) {
		this.datasource = datasource;
	}

	public List<ProxyRoute> getProxyRoutes() throws SQLException {
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		List<ProxyRoute> proxyRoutes = new ArrayList<>();
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement("select id_route, origin_url, destination_url, conn_timeout from proxy_routes");
			rs = pstmt.executeQuery();
			while (rs.next()) {
				Integer key = rs.getInt("id_route");
				String originUrl = rs.getString("origin_url");
				String destinationUrl = rs.getString("destination_url");
				Integer connTimeout = rs.getInt("conn_timeout");
				ProxyRoute proxyRoute = new ProxyRoute(key, originUrl, destinationUrl, connTimeout);
				proxyRoutes.add(proxyRoute);
			}

		} catch (SQLException e) {
			throw e;
		} finally {
			close(conn, pstmt, rs);
		}
		return proxyRoutes;
	}
	
	public DataSource getDatasource() {
		return datasource;
	}
	
	protected Connection getConnection() throws SQLException {
		return getDatasource().getConnection();
	}

	private void close(Connection conn, Statement stmt, ResultSet rs) {
		try {
			if (rs != null) {
				rs.close();
			}
		} catch (SQLException e) {
			logger.error("An error occured on closing the ResultSet", e);
		}

		try {
			if (stmt != null) {
				stmt.close();
			}
		} catch (SQLException e) {
			logger.error("An error occured on closing the statement", e);
		}

		try {
			if (conn != null) {
				conn.close();
			}
		} catch (SQLException e) {
			logger.error("An error occured on closing the connection", e);
		}
	}
	
}
