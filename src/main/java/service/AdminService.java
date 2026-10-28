
package service;

import dao.AdminDAO;

import java.sql.SQLException;
import java.util.Map;

public class AdminService {

    private final AdminDAO adminDAO;

    public AdminService(AdminDAO adminDAO) {
        this.adminDAO = adminDAO;
    }

    public Map<String, Long> obterContagens() throws SQLException {
        return adminDAO.obterContagens();
    }
}
