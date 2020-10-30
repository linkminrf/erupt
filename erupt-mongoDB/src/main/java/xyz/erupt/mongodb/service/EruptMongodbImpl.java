package xyz.erupt.mongodb.service;

import com.google.gson.JsonObject;
import lombok.SneakyThrows;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import xyz.erupt.core.service.EruptDataService;
import xyz.erupt.core.view.EruptModel;
import xyz.erupt.core.view.Page;
import xyz.erupt.core.view.TreeModel;

import javax.persistence.Id;
import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author liyuepeng
 * @date 2019-03-06.
 */
@Service
public class EruptMongodbImpl implements EruptDataService {

    @Autowired
    private MongoTemplate mongoTemplate;

    @Override
    public Object findDataById(EruptModel eruptModel, Object id) {
        Query query = new Query(Criteria.where(eruptModel.getErupt().primaryKeyCol()).is(id));
        return mongoTemplate.findOne(query, eruptModel.getClazz());
    }

    @SneakyThrows
    @Override
    public Page queryList(EruptModel eruptModel, Page page, JsonObject searchCondition, String... customCondition) {
        Query query = new Query();
        page.setTotal(mongoTemplate.count(query, eruptModel.getClazz()));
        if (page.getTotal() > 0) {
            query.limit(page.getPageSize());
            query.skip((page.getPageIndex() - 1) * page.getPageSize());
//            排序
            if (StringUtils.isNotBlank(page.getSort())) {
                for (String s : page.getSort().split(",")) {
                    if (s.split(" ")[1].contains("desc")) {
                        query.with(Sort.by(Sort.Direction.DESC, s.split(" ")[0]));
                    } else {
                        query.with(Sort.by(Sort.Direction.ASC, s.split(" ")[0]));
                    }
                }
            }
//            筛选
            for (String key : searchCondition.keySet()) {
                query.addCriteria(Criteria.where(key).is(searchCondition.get(key)));
            }
            List<Map<String, Object>> newList = new ArrayList<>();
            for (Object obj : mongoTemplate.find(query, eruptModel.getClazz())) {
                newList.add(mongoObjectToMap(obj));
            }
            page.setList(newList);
        }
        return page;
    }

    @SneakyThrows
    private Map<String, Object> mongoObjectToMap(Object obj) {
        Map<String, Object> map = new HashMap<>();
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            field.setAccessible(true);
            String fieldName = field.getName();
            if (null != field.getAnnotation(Id.class)) {
                map.put(fieldName, field.get(obj).toString());
            } else {
                map.put(fieldName, field.get(obj));
            }
        }
        return map;
    }

    @Override
    public void addData(EruptModel eruptModel, Object object) {
        mongoTemplate.insert(object);
    }

    @Override
    public void editData(EruptModel eruptModel, Object object) {
        mongoTemplate.save(object);
    }

    @Override
    public void deleteData(EruptModel eruptModel, Object object) {
        mongoTemplate.remove(object);
    }

    @Override
    public Collection<TreeModel> queryTree(EruptModel eruptModel, String... customCondition) {
        return null;
    }

    @Override
    public Collection<TreeModel> findTabTree(EruptModel eruptModel, String fieldName) {
        return null;
    }

    @Override
    public Collection<TreeModel> getReferenceTree(EruptModel eruptModel, String fieldName, Serializable dependValue, String... conditionStr) {
        return null;
    }
}
