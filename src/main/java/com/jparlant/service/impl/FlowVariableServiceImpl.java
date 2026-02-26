package com.jparlant.service.impl;

import com.jparlant.entity.FlowVariable;
import com.jparlant.mapper.FlowVariableMapper;
import com.jparlant.service.FlowVariableService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class FlowVariableServiceImpl implements FlowVariableService {
    @Autowired
    private FlowVariableMapper flowVariableMapper;

    @Override
    @Transactional
    public FlowVariable create(FlowVariable variable) {
        flowVariableMapper.insert(variable);
        return flowVariableMapper.selectById(variable.getId());
    }

    @Override
    @Transactional
    public FlowVariable update(Long id, FlowVariable variable) {
        variable.setId(id);
        flowVariableMapper.updateById(variable);
        return flowVariableMapper.selectById(id);
    }

    @Override
    @Transactional
    public void delete(Long id) {
        // 1. 先递归删除所有子项（低代码平台层级通常不深，可以用简单的递归或级联）
        List<FlowVariable> children = flowVariableMapper.selectByParentId(id);
        for (FlowVariable child : children) {
            delete(child.getId()); // 递归删除
        }
        // 2. 删除自身
        flowVariableMapper.deleteById(id);
    }

    @Override
    public List<FlowVariable> listByIntentId(Long intentId) {
        // 获取该意图下所有的变量（打平的列表）
        List<FlowVariable> allVariables = flowVariableMapper.selectByIntentId(intentId);

        // 将打平的列表转换为树形结构
        return buildTree(allVariables);
    }

    /**
     * 核心算法：将 List 转换为 Tree
     */
    private List<FlowVariable> buildTree(List<FlowVariable> list) {
        if (list == null || list.isEmpty()) return new ArrayList<>();

        // 使用 Map 存储，方便快速查找父节点
        Map<Long, FlowVariable> map = list.stream()
                .collect(Collectors.toMap(FlowVariable::getId, v -> v));

        List<FlowVariable> roots = new ArrayList<>();

        for (FlowVariable item : list) {
            Long parentId = item.getParentId();
            if (parentId == null || parentId == 0 || !map.containsKey(parentId)) {
                // 如果没有父节点，则是顶层变量
                roots.add(item);
            } else {
                // 如果有父节点，将自己加入父节点的 children 列表中
                FlowVariable parent = map.get(parentId);
                if (parent.getChildren() == null) {
                    parent.setChildren(new ArrayList<>());
                }
                parent.getChildren().add(item);
            }
        }
        return roots;
    }
}