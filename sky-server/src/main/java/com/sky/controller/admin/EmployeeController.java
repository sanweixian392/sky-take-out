package com.sky.controller.admin;

import com.sky.constant.JwtClaimsConstant;
import com.sky.context.BaseContext;
import com.sky.dto.EmployeeDTO;
import com.sky.dto.EmployeeLoginDTO;
import com.sky.dto.EmployeePageQueryDTO;
import com.sky.dto.PasswordEditDTO;
import com.sky.entity.Employee;
import com.sky.properties.JwtProperties;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.EmployeeService;
import com.sky.utils.JwtUtil;
import com.sky.vo.EmployeeLoginVO;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

/**
 * 员工管理
 */
@RestController
@RequestMapping("/admin/employee")
@Slf4j
public class EmployeeController {

    @Autowired
    private EmployeeService employeeService;
    @Autowired
    private JwtProperties jwtProperties;

    /**
     * 登录
     *
     * @param employeeLoginDTO
     * @return
     */
    @PostMapping("/login")
    public Result<EmployeeLoginVO> login(@RequestBody EmployeeLoginDTO employeeLoginDTO) {
        log.info("员工登录：{}", employeeLoginDTO);

        Employee employee = employeeService.login(employeeLoginDTO);

        //登录zm成功后，生成jwt令牌
        Map<String, Object> claims = new HashMap<>();
        claims.put(JwtClaimsConstant.EMP_ID, employee.getId());
        String token = JwtUtil.createJWT(
                jwtProperties.getAdminSecretKey(),
                jwtProperties.getAdminTtl(),
                claims);

        EmployeeLoginVO employeeLoginVO = EmployeeLoginVO.builder()
                .id(employee.getId())
                .userName(employee.getUsername())
                .name(employee.getName())
                .token(token)
                .build();

        return Result.success(employeeLoginVO);
    }


    @PostMapping
    @ApiOperation("新增员工")
    public Result<EmployeeDTO> save(@RequestBody EmployeeDTO employeeDTO){
        log.info("新增员工:{}", employeeDTO);
        employeeService.save(employeeDTO);
        return Result.success();
    }
    
    
    /**
    分页查询员工
     * @param employeePageQueryDTO
     * @return
     */
    @GetMapping("/page")
    @ApiOperation("分页查询员工")
    public Result<PageResult> page(EmployeePageQueryDTO employeePageQueryDTO){
        log.info("分页查询员工,参数为：{}",employeePageQueryDTO);
        PageResult pageResult = employeeService.pageQuery(employeePageQueryDTO);
        return Result.success(pageResult);
    }

    /**
     *
     * @param passwordEditDTO
     * @return
     */
    @PutMapping("/editPassword")
    @ApiOperation("修改密码")
    public Result<PasswordEditDTO> passwordEdit(@RequestBody PasswordEditDTO passwordEditDTO){
        //从BaseContext获取当前登录员工ID
        Long empId = BaseContext.getCurrentId();
        passwordEditDTO.setEmpId(empId);

        log.info("将密码修改为:{}",passwordEditDTO);
        employeeService.passwordEdit(passwordEditDTO);
        return Result.success();
    }


    @PutMapping
    @ApiOperation("编辑员工信息")
    public Result<EmployeeDTO> update(@RequestBody EmployeeDTO employeeDTO){
        log.info("编辑员工信息为：{}",employeeDTO);

        employeeService.update(employeeDTO);

        return Result.success();
    }


    /**
     *
     * @param employee
     * @return
     */
    @PostMapping("/status/{status}")
    @ApiOperation("启用，禁用员工账号")
    public Result<Employee> employeeStatus(Employee employee){
        log.info("员工状态设置为：{}",employee);

        employeeService.employeeStatus(employee);
        return Result.success();
    }

    /**
     *
     */
    @GetMapping("/{id}")
    @ApiOperation("根据id查询员工")
    public Result<Employee> queryById(@PathVariable Long id){
        log.info("查询id查询员工：{}",id);
        Employee emp = employeeService.queryById(id);
        return Result.success(emp);
    }

    /**
     * 退出
     *
     * @return
     */
    @PostMapping("/logout")
    public Result<String> logout() {
        return Result.success();
    }

}
