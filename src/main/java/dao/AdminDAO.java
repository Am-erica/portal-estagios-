
package dao;

import java.sql.SQLException;
import java.util.Map;

public interface AdminDAO {
    Map<String, Long> obterContagens() throws SQLException;
}
