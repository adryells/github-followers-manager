package com.wavers.server.db.queries;

import com.wavers.server.db.QueryBase;
import com.wavers.server.dtos.FollowerDTO;
import com.wavers.server.utils.ResultSetStream;

import java.sql.*;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class FollowerManager extends QueryBase {
    public void insertFollowerData(FollowerDTO userDTO) {
        try (
            Connection conn = getConnection();
            Statement stmt = conn.createStatement()
        ) {
            String checkSql = String.format("SELECT COUNT(*) FROM users WHERE githubId = %d", userDTO.getGithubId());
            ResultSet resultSet = stmt.executeQuery(checkSql);
            resultSet.next();
            int count = resultSet.getInt(1);
            resultSet.close();

            if (count == 0) {
                String insertSql = String.format(
                    "INSERT INTO users (login, avatarUrl, htmlUrl, githubId) VALUES ('%s', '%s', '%s', %d)",
                    userDTO.getLogin(), userDTO.getAvatarUrl(), userDTO.getHtmlUrl(), userDTO.getGithubId()
                );
                stmt.executeUpdate(insertSql);
            }
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }

    public List<FollowerDTO> getAllUsers() {
        try (
            Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT * FROM users");
             ResultSet result = stmt.executeQuery()
        ) {
            return ResultSetStream
                .toStream(result)
                .map(FollowerDTO::fromResultSet)
                .flatMap(Optional::stream)
                .toList();
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
            return Collections.emptyList();
        }
    }

    public void deleteFollowerData(FollowerDTO userDTO) {
        try (
            Connection conn = getConnection();
             Statement stmt = conn.createStatement()
        ) {
            String deleteSql = String.format("DELETE FROM users WHERE githubId = %d", userDTO.getGithubId());
            stmt.executeUpdate(deleteSql);
        } catch (SQLException e) {
            System.err.println("Error: " + e.getMessage());
        }
    }
}
