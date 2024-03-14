package com.caixy.userservice.controller.inner;

import com.caixy.model.dto.department.DepartAndMajorValidationResponse;
import com.caixy.model.entity.User;
import com.caixy.model.entity.UserWallet;
import com.caixy.model.vo.user.UserWorkVO;
import com.caixy.serviceclient.service.UserFeignClient;
import com.caixy.userservice.service.DepartmentInfoService;
import com.caixy.userservice.service.UserService;
import com.caixy.userservice.service.UserWalletService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 用户服务内部实现类(内部调用)
 *
 * @name: com.caixy.userservice.controller.inner.UserServiceInnerController
 * @author: CAIXYPROMISE
 * @since: 2024-02-07 00:25
 **/
@RestController
@RequestMapping("/inner")
@Slf4j
public class UserServiceInnerController implements UserFeignClient
{
    @Resource
    private UserService userService;
    @Resource
    private UserWalletService userWalletService;


    @Resource
    private DepartmentInfoService departmentInfoService;

    @Override
    @PostMapping("/validate/users")
    public Boolean validateUsers(@RequestBody List<Long> userIds)
    {
        return userService.validateUserByIds(userIds);
    }

    /**
     * 校验学院和专业信息是否合法
     *
     * @author CAIXYPROMISE
     * @version 1.0
     * @since 2024/3/6 21:01
     */
    @Override
    @PostMapping("/validate/departments-and-majors")
    public DepartAndMajorValidationResponse validateDepartmentsAndMajors(@RequestBody
                                                                         Map<Long, List<Long>> departAndMajorIds)
    {
        DepartAndMajorValidationResponse response = new DepartAndMajorValidationResponse();
        if (departAndMajorIds == null || departAndMajorIds.isEmpty())
        {
            response.setIsValid(false);
            return response;
        }
        // 提取所有的学院ID和专业ID
        Set<Long> departmentIds = departAndMajorIds.keySet();
        Set<Long> majorIds = departAndMajorIds.values().stream()
                .flatMap(List::stream)
                .collect(Collectors.toSet());

        // 执行自定义查询
        List<Map<String, Object>> validationResults = departmentInfoService.validateDepartmentsAndMajors(
                new ArrayList<>(departmentIds), new ArrayList<>(majorIds));
        // 检查查询结果是否覆盖了所有输入的ID
        Set<Long> validatedDepartmentIds = new HashSet<>();
        Set<Long> validatedMajorIds = new HashSet<>();

        for (Map<String, Object> result : validationResults)
        {
            validatedDepartmentIds.add((Long) result.get("departmentId"));
            validatedMajorIds.add((Long) result.get("majorId"));
        }
        // 确保所有输入的学院ID和专业ID都在查询结果中

        response.setIsValid(departmentIds.equals(validatedDepartmentIds)
                && majorIds.equals(validatedMajorIds));
        return response;
    }

    @Override
    @GetMapping("/get/id")
    public User getById(@RequestParam long userId)
    {
        return userService.getById(userId);
    }

    @Override
    @GetMapping("/get/account")
    public User getByAccount(@RequestParam("userId") String userAccount)
    {
        return userService.getByAccount(userAccount);
    }

    @Override
    @GetMapping("/get/account/list")
    public List<User> listUserByAccount(@RequestBody List<String> userAccount)
    {
        return userService.getByAccounts(userAccount);
    }

    @Override
    @GetMapping("/get/id/workVO")
    public UserWorkVO getUserWorkVO(@RequestParam("userId") Long userId)
    {
        return userService.getUserWorkVO(userId);
    }

    @Override
    @PostMapping("/get/ids/workVO")
    public List<UserWorkVO> getUserWorksByIds(@RequestBody List<Long> userIds)
    {
        return userService.getUserWorksByIds(userIds);
    }


    @Override
    @GetMapping("/get/ids")
    public List<User> listByIds(@RequestParam("idList") Collection<Long> idList)
    {
        return userService.listByIds(idList);
    }

    @Override
    @GetMapping("/get/wallet")
    public UserWallet getUserWallet(@RequestParam("userId") Long userId)
    {
        return userWalletService.getById(userId);
    }

}
