package xyz.erupt.generator.model;

import freemarker.ext.beans.BeansWrapper;
import freemarker.template.TemplateHashModel;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;
import xyz.erupt.annotation.Erupt;
import xyz.erupt.annotation.EruptField;
import xyz.erupt.annotation.sub_erupt.RowOperation;
import xyz.erupt.annotation.sub_erupt.Tpl;
import xyz.erupt.annotation.sub_field.Edit;
import xyz.erupt.annotation.sub_field.EditType;
import xyz.erupt.annotation.sub_field.View;
import xyz.erupt.annotation.sub_field.sub_edit.*;
import xyz.erupt.auth.model.base.HyperModel;
import xyz.erupt.generator.base.GeneratorType;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Erupt(name = "生成Erupt代码",
        rowOperation = @RowOperation(code = "preview", title = "代码预览",
                mode = RowOperation.Mode.SINGLE, icon = "fa fa-code",
                type = RowOperation.Type.TPL,
                tpl = @Tpl(path = "generator/code-skeleton.ftl",
                        tplHandler = GeneratorClass.class)))
@Entity
@Table(name = "e_generator_class")
@Getter
@Setter
public class GeneratorClass extends HyperModel implements Tpl.TplHandler {

    @EruptField(
            views = @View(title = "中文名称"),
            edit = @Edit(title = "中文名称", notNull = true)
    )
    private String name;

    @EruptField(
            views = @View(title = "实体类名"),
            edit = @Edit(title = "实体类名", notNull = true,
                    inputType = @InputType,
                    numberType = @NumberType,
                    sliderType = @SliderType(max = 999),
                    dateType = @DateType(type = DateType.Type.DATE_TIME),
                    boolType = @BoolType,
                    choiceType = @ChoiceType,
                    tagsType = @TagsType

            )
    )
    private String className;

//    @EruptField(
//            views = @View(title = "维护"),
//            edit = @Edit(title = "自动维护创建字段与更新字段")
//    )
//    private Boolean createField;

    @OneToMany(cascade = CascadeType.ALL)
    @JoinColumn(name = "CLASS_ID")
    @EruptField(
            edit = @Edit(title = "字段管理", type = EditType.TAB_TABLE_ADD)
    )
    private Set<GeneratorField> generatorFields;


    @SneakyThrows
    @Override
    public Map<String, Object> bindTplData(String[] params) {
        Map<String, Object> map = new HashMap<>();
        BeansWrapper wrapper = BeansWrapper.getDefaultInstance();
        TemplateHashModel staticModels = wrapper.getStaticModels();
        TemplateHashModel fileStatics = (TemplateHashModel) staticModels.get(GeneratorType.class.getName());
        map.put("GeneratorType", fileStatics);
        return map;
    }
}
