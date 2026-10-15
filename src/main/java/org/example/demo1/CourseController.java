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
@RequestMapping("/courses")
public class CourseController {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private final RowMapper<Course> courseMapper = new RowMapper<Course>() {
        @Override
        public Course mapRow(ResultSet rs, int rowNum) throws SQLException {
            Course c = new Course();
            c.setId(rs.getInt("id"));
            c.setName(rs.getString("name"));
            c.setTeacher(rs.getString("teacher"));
            c.setCredit(rs.getInt("credit"));
            return c;
        }
    };

    // 查询所有课程 -> 200 + 列表
    @GetMapping
    public List<Course> getAllCourses() {
        return jdbcTemplate.query("SELECT * FROM course", courseMapper);
    }

    // 查询单个课程 -> 200；不存在 -> 404
    @GetMapping("/{id}")
    public ResponseEntity<?> getCourseById(@PathVariable int id) {
        List<Course> list = jdbcTemplate.query("SELECT * FROM course WHERE id = ?", courseMapper, id);
        if (list.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "课程不存在"));
        }
        return ResponseEntity.ok(list.get(0));
    }

    // 新增课程 -> 201
    @PostMapping
    public ResponseEntity<Map<String, String>> addCourse(@RequestBody Course course) {
        jdbcTemplate.update("INSERT INTO course (name, teacher, credit) VALUES (?, ?, ?)",
                course.getName(), course.getTeacher(), course.getCredit());
        return ResponseEntity.status(HttpStatus.CREATED).body(Map.of("message", "添加成功"));
    }

    // 修改课程 -> 200；不存在 -> 404
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, String>> updateCourse(@PathVariable int id, @RequestBody Course course) {
        int rows = jdbcTemplate.update("UPDATE course SET name=?, teacher=?, credit=? WHERE id=?",
                course.getName(), course.getTeacher(), course.getCredit(), id);
        if (rows == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "课程不存在"));
        }
        return ResponseEntity.ok(Map.of("message", "修改成功"));
    }

    // 删除课程 -> 200；不存在 -> 404
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, String>> deleteCourse(@PathVariable int id) {
        int rows = jdbcTemplate.update("DELETE FROM course WHERE id = ?", id);
        if (rows == 0) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "课程不存在"));
        }
        return ResponseEntity.ok(Map.of("message", "删除成功"));
    }
}