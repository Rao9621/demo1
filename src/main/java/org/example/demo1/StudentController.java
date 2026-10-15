// @author cly202405315310
package org.example.demo1;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.web.bind.annotation.*;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Student> studentMapper = new RowMapper<Student>() {
        @Override
        public Student mapRow(ResultSet rs, int rowNum) throws SQLException {
            Student s = new Student();
            s.setId(rs.getInt("id"));
            s.setName(rs.getString("name"));
            s.setAge(rs.getInt("age"));
            s.setScore(rs.getDouble("score"));
            return s;
        }
    };

    // 查询学生：不传 keyword 返回全部，传了按姓名模糊搜索 -> 200 + 列表
    @GetMapping
    public List<Student> getAllStudents(@RequestParam(required = false) String keyword) {
        if (keyword == null || keyword.isBlank()) {
            return jdbcTemplate.query("SELECT * FROM student ORDER BY id", studentMapper);
        }
        return jdbcTemplate.query(
                "SELECT * FROM student WHERE name LIKE ? ORDER BY id",
                studentMapper, "%" + keyword.trim() + "%");
    }

    // 查询单个学生 -> 200；不存在 -> 404
    @GetMapping("/{id}")
    public ResponseEntity<?> getStudentById(@PathVariable int id) {
        List<Student> list = jdbcTemplate.query("SELECT * FROM student WHERE id = ?", studentMapper, id);
        if (list.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "学生不存在"));
        }
        return ResponseEntity.ok(list.get(0));
    }

    // 新增学生 -> 201
    @PostMapping
    public ResponseEntity<Map<String, String>> addStudent(@RequestBody Student student) {
        jdbcTemplate.update("INSERT INTO student (name, age, score) VALUES (?, ?, ?)",
                student.getName(), student.getAge(), student.getScore());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "添加成功"));
    }

    // 修改学生 -> 200；不存在 -> 404
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateStudent(@PathVariable int id, @RequestBody Student student) {
        int rows = jdbcTemplate.update("UPDATE student SET name=?, age=?, score=? WHERE id=?",
                student.getName(), student.getAge(), student.getScore(), id);
        if (rows == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "学生不存在"));
        }
        return ResponseEntity.ok(Map.of("message", "修改成功"));
    }

    // 删除学生 -> 200；不存在 -> 404
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteStudent(@PathVariable int id) {
        int rows = jdbcTemplate.update("DELETE FROM student WHERE id = ?", id);
        if (rows == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "学生不存在"));
        }
        return ResponseEntity.ok(Map.of("message", "删除成功"));
    }
}
