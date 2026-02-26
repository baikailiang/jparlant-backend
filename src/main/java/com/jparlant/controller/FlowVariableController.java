package com.jparlant.controller;



import com.jparlant.common.Result;
import com.jparlant.entity.FlowVariable;
import com.jparlant.service.FlowVariableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/variables")
public class FlowVariableController {

    @Autowired
    private FlowVariableService flowVariableService;

    // 获取意图下的变量树
    @GetMapping("/intent/{intentId}")
    public Result<List<FlowVariable>> list(@PathVariable Long intentId) {
        return Result.success(flowVariableService.listByIntentId(intentId));
    }

    @PostMapping
    public Result<FlowVariable> create(@RequestBody FlowVariable variable) {
        // 前端传入时，如果是对象的属性，需带上 parentId
        return Result.success(flowVariableService.create(variable));
    }

    @PutMapping("/{id}")
    public Result<FlowVariable> update(@PathVariable Long id, @RequestBody FlowVariable variable) {
        return Result.success(flowVariableService.update(id, variable));
    }

    @DeleteMapping("/{id}")
    public Result<Void> delete(@PathVariable Long id) {
        flowVariableService.delete(id);
        return Result.success();
    }



}
