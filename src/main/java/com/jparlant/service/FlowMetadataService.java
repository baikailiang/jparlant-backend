package com.jparlant.service;

import com.jparlant.dto.BeanWithMethodsDTO;
import com.jparlant.dto.MethodMetadataDTO;
import com.jparlant.dto.PropertySchemaDTO;
import com.jparlant.entity.FlowActionBean;
import com.jparlant.entity.FlowActionSchema;
import com.jparlant.mapper.FlowActionMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

/**
 * 流程元数据服务：负责从数据库加载业务动作及其结构定义
 */
@Service
public class FlowMetadataService {

    @Autowired
    private FlowActionMapper flowActionMapper;

    /**
     * 获取所有可用的业务动作及方法列表（含完整的参数和返回 Schema 树）
     */
    public List<BeanWithMethodsDTO> getAllActionsFromDb() {
        // 1. 一次性获取所有 Bean 及其关联的方法 (MyBatis 关联查询)
        List<FlowActionBean> beanEntities = flowActionMapper.selectAllBeansAndMethods();

        // 2. 一次性获取所有 Schema 节点定义 (包含 PARAMETER 和 RETURN 类型)
        List<FlowActionSchema> allSchemas = flowActionMapper.selectAllSchemas();

        // 3. 将所有 Schema 节点按 methodId 进行预分组，避免在循环中重复过滤
        Map<Long, List<FlowActionSchema>> schemaByMethodMap = allSchemas.stream()
                .collect(Collectors.groupingBy(FlowActionSchema::getMethodId));

        // 4. 组装最终的 DTO 结构
        return beanEntities.stream().map(bean -> {
            // 将方法实体转换为 MethodMetadataDTO
            List<MethodMetadataDTO> methodList = bean.getMethods().stream().map(m -> {
                // 获取当前方法对应的所有 Schema 行数据
                List<FlowActionSchema> methodSchemas = schemaByMethodMap.getOrDefault(m.getId(), Collections.emptyList());

                // 组装入参树 (PARAMETER)
                List<PropertySchemaDTO> parameters = buildTree(methodSchemas, "PARAMETER");

                // 组装返回结果树 (RETURN)
                List<PropertySchemaDTO> returns = buildTree(methodSchemas, "RETURN");

                return new MethodMetadataDTO(
                        m.getMethodName(),
                        m.getDisplayName(),
                        parameters,
                        returns
                );
            }).collect(Collectors.toList());

            // 包装成 Bean 层级的 DTO
            return new BeanWithMethodsDTO(
                    bean.getBeanName(),
                    bean.getDisplayName(),
                    methodList
            );
        }).collect(Collectors.toList());
    }

    /**
     * 在内存中根据平铺的 Schema 列表高效构建树形结构 (O(n) 复杂度)
     *
     * @param schemas 属于该方法的所有 Schema 原始行
     * @param type    Schema类型：PARAMETER(入参) 或 RETURN(返回)
     * @return 根节点列表
     */
    private List<PropertySchemaDTO> buildTree(List<FlowActionSchema> schemas, String type) {
        // 1. 过滤出对应类型的节点
        List<FlowActionSchema> filtered = schemas.stream()
                .filter(s -> type.equals(s.getSchemaType()))
                .collect(Collectors.toList());

        if (filtered.isEmpty()) {
            return Collections.emptyList();
        }

        // 2. 第一遍遍历：将所有实体转换为 DTO，并存入 Map (Key 为节点 ID)
        // 使用 LinkedHashMap 保持数据库中可能的排序顺序
        Map<Long, PropertySchemaDTO> nodeMap = new LinkedHashMap<>();
        for (FlowActionSchema s : filtered) {
            PropertySchemaDTO dto = new PropertySchemaDTO(
                    s.getName(),
                    s.getType(),
                    s.getDescription(),
                    s.getIsComplex() == 1,
                    new ArrayList<>() // 初始化子节点列表
            );
            nodeMap.put(s.getId(), dto);
        }

        // 3. 第二遍遍历：根据 parentId 建立父子映射关系
        List<PropertySchemaDTO> roots = new ArrayList<>();
        for (FlowActionSchema s : filtered) {
            PropertySchemaDTO currentNode = nodeMap.get(s.getId());

            if (s.getParentId() == null || s.getParentId() == 0) {
                // 没有父节点，说明是第一层（根节点）
                roots.add(currentNode);
            } else {
                // 寻找父节点并将自己加入父节点的 children 列表中
                PropertySchemaDTO parentNode = nodeMap.get(s.getParentId());
                if (parentNode != null) {
                    parentNode.getChildren().add(currentNode);
                }
            }
        }
        return roots;
    }
}