package com.hcc.annotation_compile;

import com.google.auto.service.AutoService;
import com.hcc.annotation.BindView;
import com.hcc.annotation.OnClick;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Filer;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.PackageElement;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import javax.tools.JavaFileObject;

@AutoService(Processor.class)//注册我这个类 就是注解处理器
public class AnnotationCompiler extends AbstractProcessor {

    Filer mFiler;//这个类是用来生产文件的

    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        mFiler = processingEnv.getFiler();
        logUtil("AnnotationCompiler init");

    }
   //声明注解处理器支持的java版本
    @Override
    public SourceVersion getSupportedSourceVersion() {
        logUtil("getSupportedSourceVersion");
        return processingEnv.getSourceVersion();
    }
   //声明注解处理器支持的注解有哪些
    @Override
    public Set<String> getSupportedAnnotationTypes() {
        logUtil("getSupportedAnnotationTypes");
        Set<String> types = new HashSet<>();
        types.add(BindView.class.getCanonicalName());
        types.add(OnClick.class.getCanonicalName());
        return types;
    }

    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        logUtil("process");
       Map<TypeElement,ElementForType> map = findAndParseTarget(roundEnv);

       if (map.size()>0){
           logUtil("map>0");
           Iterator<TypeElement> iterator = map.keySet().iterator();
           Writer writer = null;
           if (iterator.hasNext()){
               TypeElement typeElement = iterator.next();
               ElementForType elementForType = map.get(typeElement);
               String clazzName = typeElement.getSimpleName().toString();
               String packageName = getPackageName(typeElement);
               String newClazzName = clazzName+"$$ViewBinder";

               try {
                   JavaFileObject file = mFiler.createSourceFile(packageName + "." + newClazzName);
                   writer = file.openWriter();
                   StringBuffer stringBuffer = getStringBuffer(packageName,newClazzName,typeElement,elementForType);
                   writer.write(stringBuffer.toString());
               } catch (IOException e) {
                   e.printStackTrace();
               }finally {
                   if (writer!=null){
                       try {
                           writer.close();
                       } catch (IOException e) {
                           e.printStackTrace();
                       }
                   }
               }

           }
       }else {
           logUtil("map<=0");
       }

        return true;
    }

    private StringBuffer getStringBuffer(String packageName, String newClazzName, TypeElement typeElement,ElementForType elementForType) {
        logUtil("getStringBuffer");
        StringBuffer stringBuffer = new StringBuffer();
        stringBuffer.append("package "+packageName+";\n");
        stringBuffer.append("import android.view.View;\n");
        stringBuffer.append("public "+newClazzName+"final "+typeElement.getQualifiedName()+" target){\n");
        if (elementForType!=null && elementForType.getViewElements()!=null &&  elementForType.getViewElements().size()>0){
            List<VariableElement> elements = elementForType.getViewElements();
            for (VariableElement viewElement:elements){
                //获取到类型
                TypeMirror mirror = viewElement.asType();
                //控件名
                Name name = viewElement.getSimpleName();
                int resId = viewElement.getAnnotation(BindView.class).value();
                stringBuffer.append("target."+name+" =("+mirror+")target.findViewById("+resId+");\n");
              }

        }
        if (elementForType!=null && elementForType.getMethodElement()!=null && elementForType.getMethodElement().size()>0){
            List<ExecutableElement> elements = elementForType.getMethodElement();
            for (ExecutableElement methodElement:elements){
                int[] resIds = methodElement.getAnnotation(OnClick.class).value();
                String name = methodElement.getSimpleName().toString();
                for (int resId:resIds){
                    stringBuffer.append("target.findViewById("+resId+")).setOnClickListener(new View.OnClickListener(){\n");
                    stringBuffer.append("public void onClick(View p0){\n");
                    stringBuffer.append("target."+name+"(p0);\n");
                    stringBuffer.append("}\n);\n");

                }
            }
        }
        stringBuffer.append("}\n});\n");

        return stringBuffer;

    }

    private Map<TypeElement, ElementForType> findAndParseTarget(RoundEnvironment env) {
        Map<TypeElement, ElementForType> map = new HashMap<>();
        Set<? extends Element> viewElements = env.getElementsAnnotatedWith(BindView.class);
        Set<? extends Element> methodElements = env.getElementsAnnotatedWith(OnClick.class);
        for(Element viewElement :viewElements){
            //成员变量结点
            VariableElement variableElement = (VariableElement) viewElement;
            //类结点
            TypeElement typeElement = (TypeElement) variableElement.getEnclosingElement();
            ElementForType elementForType = map.get(typeElement);
            List<VariableElement> variableElementList;
            if (elementForType!=null){
                //MAP中对应类的封装对象中的成员变量的节点集合
                variableElementList = elementForType.getViewElements();
                if (variableElementList == null){
                    variableElementList = new ArrayList<>();
                    elementForType.setViewElements(variableElementList);
                }
            }else {
                elementForType  = new ElementForType();
                variableElementList = new ArrayList<>();
                elementForType.setViewElements(variableElementList);
                if (map.containsKey(typeElement)){
                    map.put(typeElement,elementForType);
                }
            }

            variableElementList.add(variableElement);

        }
        for(Element methodElement :methodElements){
            //成员变量结点
            ExecutableElement executableElement = (ExecutableElement) methodElement;
            //类结点
            TypeElement typeElement = (TypeElement) executableElement.getEnclosingElement();
            ElementForType elementForType = map.get(typeElement);
            List<ExecutableElement> methodElementList;
            if (elementForType!=null){
                //MAP中对应类的封装对象中的成员变量的节点集合
                methodElementList = elementForType.getMethodElement();
                if (methodElementList == null){
                    methodElementList = new ArrayList<>();
                    elementForType.setMethodElement(methodElementList);
                }
            }else {
                elementForType  = new ElementForType();
                methodElementList = new ArrayList<>();
                elementForType.setMethodElement(methodElementList);
                if (map.containsKey(typeElement)){
                    map.put(typeElement,elementForType);
                }
            }

            methodElementList.add(executableElement);
        }

        return map;
    }

    public void logUtil(String log){
        Messager msg = processingEnv.getMessager();
        msg.printMessage(Diagnostic.Kind.ERROR,log);
    }

    public String getPackageName(Element typeElement){
        PackageElement packageElement = processingEnv.getElementUtils().getPackageOf(typeElement);
        Name name = packageElement.getQualifiedName();
        return name.toString();

    }
}