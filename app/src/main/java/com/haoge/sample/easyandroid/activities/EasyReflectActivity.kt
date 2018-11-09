package com.haoge.sample.easyandroid.activities

import android.annotation.SuppressLint
import butterknife.OnClick
import com.haoge.easyandroid.easy.EasyFormatter
import com.haoge.easyandroid.easy.EasyLog
import com.haoge.easyandroid.easy.EasyReflect
import com.haoge.sample.easyandroid.BaseActivity
import com.haoge.sample.easyandroid.R
import java.util.*

/**
 * @author haoge on 2018/5/22
 */
@SuppressLint("SetTextI18n")
class EasyReflectActivity:BaseActivity() {

    val log = EasyLog.DEFAULT

    override fun getLayoutId(): Int {
        return R.layout.activity_reflect
    }

    @OnClick(R.id.createWithInstance)
    fun createWithInstance() {
        val reflect = EasyReflect.create(Test())
        log.d("直接使用具体实例进行创建：\n$reflect")
    }

    @OnClick(R.id.createWithClass)
    fun createWithClass() {
        val reflect = EasyReflect.create(Test::class.java)
        log.d("只使用class进行创建：\n$reflect")
    }

    @OnClick(R.id.createWithClassName)
    fun createWithClassName() {
        val reflect = EasyReflect.create("com.haoge.sample.easyandroid.activities.Test")
        log.d("只使用class类全名进行创建：\n$reflect")
    }

    @OnClick(R.id.createByConstructor)
    fun createByConstructor() {
        val reflect = EasyReflect.create(Test::class.java).instance("自定义参数")
        log.d("使用class与构造参数进行创建：\n$reflect")
    }

    @OnClick(R.id.getInstanceValue)
    fun getInstanceValue() {
        val reflect = EasyReflect.create(Test::class.java).instance("构造函数")
        log.d("获取创建出来的实例：${reflect.get<Test>()}")
    }

    @OnClick(R.id.getFieldValueByNameWithReflect)
    fun getFieldValueByNameWithReflect() {
        val reflect = EasyReflect.create(Test::class.java)
        log.e("通过创建出的reflect：\n$reflect \n获取到字段name的值为：${reflect.getFieldValue<String>("name")}")
    }

    @OnClick(R.id.setFieldValueByNameWithReflect)
    fun setFieldValueByNameWithReflect() {
        val reflect = EasyReflect.create(Test::class.java)
        log.e("字段被设置之前的reflect: \n$reflect")
        reflect.setField("name", "故意设置的")
        log.e("字段被设置之后的reflect: \n$reflect")
    }

    @OnClick(R.id.callMethodWithReflect)
    fun callMethodWithReflect() {
        val reflect = EasyReflect.create(Test())
        reflect.call("invoked", "invoke param")
    }

    @OnClick(R.id.callMethodWithReturnReflect)
    fun callMethodWithReturnReflect() {
        val reflect = EasyReflect.create(Test())
        log.d("调用方法前的reflect: \n$reflect")
        val newReflect = reflect.callWithReturn("invoked", "invoke")
        log.d("调用方法之后的reflect: \n$newReflect")
    }

    @OnClick(R.id.getFieldsWithReflect)
    fun getFieldsWithReflect() {
        val reflect = EasyReflect.create(Test::class.java)
        log.d("Test类所有字段：\n${reflect.getFields().easyFormat()}")
    }

    @OnClick(R.id.getMethodsWithReflect)
    fun getMethodsWithReflect() {
        val reflect = EasyReflect.create(Test::class.java)
        log.d("Test类所有方法：\n${reflect.getMethods().easyFormat()}")
    }

    @OnClick(R.id.getConstructorsWithReflect)
    fun getConstructorsWithReflect() {
        val reflect = EasyReflect.create(Test::class.java)
        log.d("Test类所有构造函数：\n${reflect.getConstructors().easyFormat()}")
    }

    @OnClick(R.id.callStaticMethod)
    fun callStaticMethod() {
        val reflect = EasyReflect.create(Test::class.java)
        reflect.call("print", "自定义参数")
    }

    @OnClick(R.id.callMethodWithVararg)
    fun callMethodWithVararg() {
        val reflect = EasyReflect.create(Test::class.java)
        // 调用只含有可变参数的方法
        reflect.call("withVararg", 1, arrayOf("This", "is", "WithVararg"))
        reflect.call("onlyVararg", arrayOf("Hello", "World", "onlyVararg"))
        reflect.instance(arrayOf("Hello", "World", "Constructor"))
    }

    @OnClick(R.id.getStaticField)
    fun getStaticField() {
        val reflect = EasyReflect.create(Test::class.java)
        val field = reflect.getField("staticValue")
        log.d("获取到的字段为：\n${field.easyFormat()} \n 值为：${field.getValue<String>()}")
    }

    @OnClick(R.id.callMethodWithProxy)
    fun callMethodWithProxy() {
        val reflect = EasyReflect.create(Test::class.java)
        val proxy = reflect.proxy(TestProxy::class.java)
        proxy.invoked()
        val result = proxy.invoked("使用动态代理调用invoked方法参数")
        log.e("调用invoked参数返回值：$result")
        proxy.print("使用动态代理调用print方法参数")
        EasyLog.DEFAULT.d("notDefine result :${proxy.notDefine()}")// 调用一个不存在Test类中的方法。
        EasyLog.DEFAULT.d("Test.name : ${proxy.getName()}")// 读取name字段的值
    }
}

// 用于进行测试的类
class Test private constructor(private val name:String){
    constructor():this("默认名字")
    constructor(vararg name:String):this(Arrays.toString(name))

    override fun toString(): String {
        return "Test(name='$name')"
    }

    fun invoked() {
        EasyLog.DEFAULT.e("无参数的invoked方法被执行")
    }

    fun invoked(name:String){
        EasyLog.DEFAULT.e("Test的invoked方法被执行。参数为$name")
    }

    fun onlyVararg(vararg name:String) {
        EasyLog.DEFAULT.d(name)
    }

    fun withVararg(perfix:Int, vararg name:String) {
        EasyLog.DEFAULT.d("perfix = $perfix + name = ${name.easyFormat()}")
    }

    companion object {
        @JvmStatic
        private fun print(message:String) {
            EasyLog.DEFAULT.e("静态方法被调用,传入参数：$message")
        }

        @JvmStatic
        private val staticValue = "静态文本"
    }
}

interface TestProxy {
    fun invoked()           // 对应Test.invoked()无参方法
    fun invoked(name:String)// 对应Test.invoked(name)方法
    fun print(message:String)// 对应Test.print方法
    fun notDefine():Int// 无对应方法
    fun getName():String // 读取Test.name字段

}

data class A(var b:B?)

data class B(var a:A?)

private fun Any.easyFormat() = EasyFormatter.DEFAULT.format(this)