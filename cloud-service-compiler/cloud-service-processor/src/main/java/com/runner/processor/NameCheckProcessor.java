package com.runner.processor;

import com.google.auto.service.AutoService;

import java.util.EnumSet;
import java.util.Set;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.ExecutableElement;
import javax.lang.model.element.Name;
import javax.lang.model.element.TypeElement;
import javax.lang.model.element.VariableElement;
import javax.lang.model.util.ElementScanner7;
import javax.tools.Diagnostic.Kind;

@SupportedAnnotationTypes(value = "com.runner.processor.MyAnnotation")
@SupportedSourceVersion(value = SourceVersion.RELEASE_8)
@AutoService(Processor.class)
public class NameCheckProcessor extends AbstractProcessor {

    private NameCheck nameCheck;

    /**
     * 初始化检查插件
     * 继承了AbstractProcessor的注解处理器可以直接访问继承了processingEnv，它代表上下文环境，要穿件新的代码、向编译器输出信息、获取其他工具类都需要用到这个实例
     *
     * @param processingEnv ProcessingEnvironment
     */
    @Override
    public synchronized void init(ProcessingEnvironment processingEnv) {
        super.init(processingEnv);
        this.nameCheck = new NameCheck(processingEnv);
    }


    /**
     * 对语法树的各个节点今夕名称检查
     * java编译器在执行注解处理器代码时要调用的过程
     *
     * @param annotations
     * @param roundEnv
     * @return
     */
    @Override
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        if (!roundEnv.processingOver()) {
            for (Element element : roundEnv.getRootElements()) {
                nameCheck.check(element);
            }
        }
        return false;
    }

    /**
     * 程序名称规范的编译期插件
     * 程序名称规范的编译器插件 如果程序命名不合规范，将会输出一个编译器的Warning信息
     *
     * @author kevin
     */
    public static class NameCheck {
        Messager messager = null;
        public NameCheckScanner nameCheckScanner;

        private NameCheck(ProcessingEnvironment processingEnv) {
            messager = processingEnv.getMessager();
            nameCheckScanner = new NameCheckScanner(processingEnv);
        }

        /**
         * 对Java程序明明进行检查，根据《Java语言规范（第3版）》6.8节的要求，Java程序命名应当符合下列格式：
         * <ul>
         * <li>类或接口：符合驼式命名法，首字母大写。
         * <li>方法：符合驼式命名法，首字母小写。
         * <li>字段：
         * <ul>
         * <li>类，实例变量：符合驼式命名法，首字母小写。
         * <li>常量：要求全部大写
         * </ul>
         * </ul>
         *
         * @param element
         */
        public void check(Element element) {
            nameCheckScanner.scan(element);
        }

        /**
         * 名称检查器实现类，继承了1.6中新提供的ElementScanner6<br>
         * 将会以Visitor模式访问抽象语法数中得元素
         *
         * @author kevin
         */
        public static class NameCheckScanner extends ElementScanner7<Void, Void> {
            Messager messager = null;

            public NameCheckScanner(ProcessingEnvironment processingEnv) {
                this.messager = processingEnv.getMessager();
            }

            /**
             * 此方法用于检查Java类
             */
            @Override
            public Void visitType(TypeElement e, Void p) {
                scan(e.getTypeParameters(), p);
                checkCamelCase(e, true);
                super.visitType(e, p);
                return null;
            }

            /**
             * 检查方法命名是否合法
             */
            @Override
            public Void visitExecutable(ExecutableElement e, Void p) {
                if (e.getKind() == ElementKind.METHOD) {
                    Name name = e.getSimpleName();
                    if (name.contentEquals(e.getEnclosingElement().getSimpleName())) {
                        messager.printMessage(Kind.WARNING, "一个普通方法:" + name + " 不应当与类名重复，避免与构造函数产生混淆", e);
                        checkCamelCase(e, false);
                    }
                }
                super.visitExecutable(e, p);
                return null;
            }

            /**
             * 检查变量是否合法
             */
            @Override
            public Void visitVariable(VariableElement e, Void p) {
                /* 如果这个Variable是枚举或常量，则按大写命名检查，否则按照驼式命名法规则检查 */
                if (e.getKind() == ElementKind.ENUM_CONSTANT || e.getConstantValue() != null || heuristicallyConstant(e)) {
                    checkAllCaps(e);
                } else {
                    checkCamelCase(e, false);
                }
                super.visitVariable(e, p);
                return null;
            }

            /**
             * 判断一个变量是否是常量
             *
             * @param e
             * @return
             */
            private boolean heuristicallyConstant(VariableElement e) {
                if (e.getEnclosingElement().getKind() == ElementKind.INTERFACE) {
                    return true;
                } else if (e.getKind() == ElementKind.FIELD && e.getModifiers().containsAll(EnumSet.of(javax.lang.model.element.Modifier.FINAL, javax.lang.model.element.Modifier.STATIC, javax.lang.model.element.Modifier.PUBLIC))) {
                    return true;
                }
                return false;
            }

            /**
             * 检查传入的Element是否符合驼式命名法，如果不符合，则输出警告信息
             *
             * @param e
             * @param initialCaps
             */
            private void checkCamelCase(Element e, boolean initialCaps) {
                String name = e.getSimpleName().toString();
                boolean previousUpper = false;
                boolean conventional = true;
                int firstCodePoint = name.codePointAt(0);
                if (Character.isUpperCase(firstCodePoint)) {
                    previousUpper = true;
                    if (!initialCaps) {
                        messager.printMessage(Kind.WARNING, "名称：" + name + " 应当已小写字符开头", e);
                        return;
                    }
                } else if (Character.isLowerCase(firstCodePoint)) {
                    if (initialCaps) {
                        messager.printMessage(Kind.WARNING, "名称:" + name + " 应当已大写字母开否", e);
                        return;
                    }
                } else {
                    conventional = false;
                }
                if (conventional) {
                    int cp = firstCodePoint;
                    for (int i = Character.charCount(cp); i < name.length(); i += Character.charCount(cp)) {
                        cp = name.codePointAt(i);
                        if (Character.isUpperCase(cp)) {
                            if (previousUpper) {
                                conventional = false;
                                break;
                            }
                            previousUpper = true;
                        } else {
                            previousUpper = false;
                        }
                    }
                }
                if (!conventional) {
                    messager.printMessage(Kind.WARNING, "名称:" + name + "应当符合驼式命名法（Camel Case Names）", e);
                }
            }

            /**
             * 大写命名检查，要求第一个字符必须是大写的英文字母，其余部分可以下划线或大写字母
             *
             * @param e
             */
            private void checkAllCaps(VariableElement e) {
                String name = e.getSimpleName().toString();
                boolean conventional = true;
                int firstCodePoint = name.codePointAt(0);
                if (!Character.isUpperCase(firstCodePoint)) {
                    conventional = false;
                } else {
                    boolean previousUnderscore = false;
                    int cp = firstCodePoint;
                    for (int i = Character.charCount(cp); i < name.length(); i += Character.charCount(cp)) {
                        cp = name.codePointAt(i);
                        if (cp == (int) '_') {
                            if (previousUnderscore) {
                                conventional = false;
                                break;
                            }
                            previousUnderscore = true;
                        } else {
                            previousUnderscore = false;
                            if (!Character.isUpperCase(cp) && !Character.isDigit(cp)) {
                                conventional = false;
                                break;
                            }
                        }

                    }
                }
                if (!conventional) {
                    messager.printMessage(Kind.WARNING, "常量:" + name + " 应该全部以大写字母" + "或下划线命名，并且以字符开否", e);
                }
            }
        }
    }

}
