package org.bohan.minispring.beans.factory.support

import org.bohan.minispring.beans.BeansException
import org.bohan.minispring.beans.factory.*
import org.bohan.minispring.beans.factory.config.BeanDefinition
import org.bohan.minispring.beans.factory.config.BeanDefinitionHolder
import org.bohan.minispring.beans.factory.config.ConfigurableBeanFactory
import org.slf4j.LoggerFactory
import java.util.Collections
import java.util.concurrent.ConcurrentHashMap

/**
 * BeanFactory接口的默认实现
 * 基于列表的bean工厂实现，支持单例bean的注册、别名机制、依赖注入和循环依赖处理
 *
 * @author Bohan
 */
open class DefaultListableBeanFactory:
    AbstractAutowireCapableBeanFactory(), ConfigurableListableBeanFactory, BeanDefinitionRegistry{

    private val logger = LoggerFactory.getLogger(DefaultListableBeanFactory::class.java)

    /** 存储单例 Bean 的容器 - 一级缓存 - 存储完全初始化好的单例 Bean */
    private val singletonObjects = ConcurrentHashMap<String, Any>(256)
    /** 存储单例 Bean 的容器 - 二级缓存 - 存储早期的单例 Bean 对象（未完全初始化）*/
    private val earlySingletonObjects = ConcurrentHashMap<String, Any>(16)
    /** 存储单例 Bean 的容器 - 三级缓存 - 存储单例 Bean 的工厂对象 */
    private val singletonFactories = ConcurrentHashMap<String, ObjectFactory<*>>(16)
    /** 当前 Bean 名称的集合 */
    private val singletonCurrentlyInCreation: MutableSet<String> = Collections.newSetFromMap(ConcurrentHashMap(16))
    /** 存储 Bean 定义的容器 */
    private val beanDefinitionMap = ConcurrentHashMap<String, BeanDefinition>(256)
    /** 储存 BeanDefinition 名称的容器，按照注册顺序存储 */
    @Volatile
    private var beanDefinitionNames = mutableListOf<String>()
    /** 储存合并的 BeanDefinition 的容器 */
    private val mergedBeanDefinitions = ConcurrentHashMap<String, BeanDefinition>(256)

    private var beanClassLoader = Thread.currentThread().contextClassLoader
    private lateinit var parentBeanFactory: ConfigurableBeanFactory
    private val dependentBeanMap = ConcurrentHashMap<String, MutableSet<String>>(64)
    private val dependenciesForBeanMap = ConcurrentHashMap<String, MutableSet<String>>(64)
    private val beansInCreation: MutableSet<String> =
        Collections.newSetFromMap(ConcurrentHashMap(16))
    private val dependencyGraph = ConcurrentHashMap<String, MutableSet<String>>(64)

    override fun registerBeanDefinition(beanName: String, beanDefinition: BeanDefinition) {
        // 检查是否存在旧的bean定义
        val oldBeanDefinition = this.beanDefinitionMap[beanName]
        if (oldBeanDefinition != null) {
            // 如果作用域发生变化，需要清理相关缓存
            if (oldBeanDefinition.getScope() != beanDefinition.getScope()) {
                cleanUpSingletonCache(beanName)
                // 移除旧的bean定义
                this.beanDefinitionMap.remove(beanName)
                this.mergedBeanDefinitions.remove(beanName)
                val aliases = getAliases(beanName)
                aliases.forEach { alias ->
                    cleanUpSingletonCache(alias)
                    this.mergedBeanDefinitions.remove(alias)
                }
            }
        }

        this.beanDefinitionMap[beanName] = beanDefinition

        // 如果是新的bean定义，添加到名称列表中
        if (! this.beanDefinitionNames.contains(beanName)) {
            this.beanDefinitionNames.add(beanName)
        }

        logger.debug("Registered bean definition for bean named '{}'", beanName)
    }

    override fun removeBeanDefinition(beanName: String) {
        if (! containsBeanDefinition(beanName)) {
            throw BeansException("No bean named '$beanName' is defined")
        }

        this.beanDefinitionMap.remove(beanName)
        this.beanDefinitionNames.remove(beanName)

        logger.debug("Removed bean definition for bean named '{}'", beanName)
    }

    override fun getBeanDefinition(beanName: String): BeanDefinition {
        val canonicalName = canonicalName(beanName)
        val beanDefinition = this.beanDefinitionMap[beanName]
        if (beanDefinition == null) {
            if (getParentBeanFactory() is DefaultListableBeanFactory) {
                return (getParentBeanFactory() as DefaultListableBeanFactory)
                    .getBeanDefinition(canonicalName)
            }
            throw BeansException("No bean named '$beanName' is defined")
        }

        // 如果有合并的bean定义，返回合并后的
        val mergedBeanDefinition = this.mergedBeanDefinitions[canonicalName]
        if (mergedBeanDefinition != null) {
            return mergedBeanDefinition
        }

        return beanDefinition
    }

    override fun containsBeanDefinition(beanName: String): Boolean {
        return this.beanDefinitionMap.containsKey(beanName)
    }

    override fun getBeanDefinitionNames(): Array<String> {
        return this.beanDefinitionNames.toTypedArray()
    }

    override fun getBeanDefinitionCount(): Int {
        return beanDefinitionMap.size
    }

    override fun registerSingleton(beanName: String, singletonObject: Any) {
        this.singletonObjects[beanName] = singletonObject
        logger.debug("Registered singleton bean named '{}'", beanName)
    }

    override fun getSingleton(beanName: String): Any? {
        return getSingleton(beanName, true)
    }

    override fun applyBeanPostProcessorsBeforeInitialization(existingBean: Any, beanName: String): Any {
        var result = existingBean

        // 应用BeanPostProcessor的前置处理
        getBeanPostProcessor().forEach { beanPostProcessor ->
            val current = beanPostProcessor.postProcessBeforeInitialization(result, beanName) ?: return result
            result = current
        }

        return result
    }

    override fun applyBeanPostProcessorAfterInitialization(existingBean: Any, beanName: String): Any {
        var result = existingBean

        // 应用BeanPostProcessor的前置处理
        getBeanPostProcessor().forEach { beanPostProcessor ->
            val current = beanPostProcessor.postProcessAfterInitialization(result, beanName) ?: return result
            result = current
        }

        return result
    }

    override fun preInstantiateSingletons() {
        val beanNames = this.beanDefinitionNames.toList()
        beanNames.forEach { beanName ->
            val beanDefinition = getBeanDefinition(beanName)
            if (beanDefinition.isSingleton()) {
                getBean(beanName)
                logger.debug("Pre-instantiated singleton bean named '{}'", beanName)
            }
        }
    }

    override fun ensureAllSingletonsInstantiate() {
        preInstantiateSingletons()
    }

    override fun getType(name: String): Class<*>? {
        val beanName = canonicalName(name)

        // 首先检查已经实例化的单例
        val singleton = getSingleton(beanName)
        if (singleton != null) {
            return singleton.javaClass
        }

        // 然后检查bean定义
        val beanDefinition = getBeanDefinition(beanName)

        return beanDefinition.getBeanClass()
    }

    override fun setBeanClassLoader(beanClassLoader: ClassLoader) {
        this.beanClassLoader = beanClassLoader
    }

    override fun getBeanClassLoader(): ClassLoader? {
        return beanClassLoader
    }

    override fun setParentBeanFactory(parentBeanFactory: ConfigurableBeanFactory) {
        this.parentBeanFactory = parentBeanFactory
    }

    override fun getParentBeanFactory(): BeanFactory? {
        return this.parentBeanFactory
    }

    override fun containsLocalBean(name: String): Boolean {
        val beanName = transformedBeanName(name)
        return containsBeanDefinition(beanName) || containsBean(beanName)
    }

    override fun containsBean(name: String): Boolean {
        val beanName = transformedBeanName(name)
        return if (containsLocalBean(beanName)) true else (parentBeanFactory != null &&
        parentBeanFactory.containsBean(beanName))
    }

    override fun isSingleton(name: String): Boolean {
        val beanName = transformedBeanName(name)

        if (containsBeanDefinition(beanName)) {
            val beanDefinition = getBeanDefinition(beanName)
            return beanDefinition.isSingleton()
        }

        val parentBeanFactory = getParentBeanFactory()
        return parentBeanFactory != null && parentBeanFactory.isSingleton(name)
    }

    override fun isPrototype(name: String): Boolean {
        val beanName = transformedBeanName(name)

        if (containsBeanDefinition(beanName)) {
            val beanDefinition = getBeanDefinition(beanName)
            return beanDefinition.isPrototype()
        }

        val parentBeanFactory = getParentBeanFactory()
        return parentBeanFactory != null && parentBeanFactory.isPrototype(name)
    }

    override fun registerDependentBean(beanName: String, dependentBeanName: String) {
        val canonicalName = transformedBeanName(beanName)
        synchronized(dependencyGraph) {
            val dependencies = dependencyGraph.computeIfAbsent(canonicalName) { LinkedHashSet<String>() }
            dependencies.add(dependentBeanName)
        }
    }

    override fun getDependentBeans(beanName: String): Array<String> {
        val dependentBeans = this.dependentBeanMap[beanName]

        return dependentBeans?.toTypedArray() ?: arrayOf()
    }

    override fun getDependenciesForBean(beanName: String): Array<String> {
        val dependencies = this.dependenciesForBeanMap[beanName]

        return dependencies?.toTypedArray() ?: arrayOf()
    }

    override fun getBeanNamesForType(type: Class<*>): Array<String> {
        val result = mutableListOf<String>()

        // 检查已实例化的单例
        singletonObjects.entries.forEach { entry ->
            if (type.isInstance(entry.value)) {
                result.add(entry.key)
            }
        }

        // 检查bean定义
        beanDefinitionNames.forEach { beanName ->
            if (result.contains(beanName)) {
                return@forEach
            }
            val beanDefinition = getBeanDefinition(beanName)
            val beanClass = beanDefinition.getBeanClass()
            if (type.isAssignableFrom(beanClass)) {
                result.add(beanName)
            }
        }

        return result.toTypedArray()
    }

    override fun <T> getBeansOfType(type: Class<T>): Map<String, T> {
        val result = mutableMapOf<String, T>()
        beanDefinitionNames.forEach { beanName ->
            val beanDefinition = getBeanDefinition(beanName)
            val beanClass = beanDefinition.getBeanClass()
            if (type.isAssignableFrom(beanClass)) {
                @Suppress("UNCHECKED_CAST")
                result[beanName] = getBean(beanName) as T
            }
        }

        return result
    }

    override fun <T : Annotation> getBeansWithAnnotation(annotationType: T): Map<String, Any> {
        val result = mutableMapOf<String, Any>()
        beanDefinitionNames.forEach { beanName ->
            val beanDefinition = getBeanDefinition(beanName)
            val beanClass = beanDefinition.getBeanClass()
            if (beanClass.isAnnotationPresent(annotationType.javaClass)) {
                result[beanName] = getBean(beanName)
            }
        }

        return result
    }

    override fun <A : Annotation> findAnnotationOnBean(beanName: String, annotationType: Class<A>): A {
        val beanDefinition = getBeanDefinition(beanName)
        val beanClass = beanDefinition.getBeanClass()

        return beanClass.getAnnotation(annotationType)
    }

    override fun <T> getBean(requiredType: Class<T>): T {
        val beanNames = getBeanNamesForType(requiredType)
        when(beanNames.size) {
            0 -> throw BeansException("No bean type '${requiredType.name}' is defined")
            1 -> return getBean(beanNames[0], requiredType)
            else -> throw BeansException("More than one bean of type '${requiredType.name}' is defined: " +
                    beanNames.joinToString(",")
            )
        }
    }

    override fun getBean(name: String): Any {
        return doGetBean(name, null)
    }

    override fun <T> getBean(name: String, requiredType: Class<T>): T {
        return doGetBean(name, requiredType)
    }

    @Throws(BeansException::class)
    override fun createBean(beanName: String, beanDefinition: BeanDefinition): Any {
        try {
            // 如果是单例且有构造器参数，在创建实例前检测循环依赖
            if (beanDefinition.isSingleton() && (beanDefinition.getConstructorArgumentValues().size) > 0) {
                beforeSingletonCreation(beanName)
                try {
                    // 创建bean实例
                    val bean = createBeanInstance(beanDefinition)

                    // 填充属性
                    populateBean(beanName, bean, beanDefinition)

                    // 初始化bean
                    val exposedObject = initializeBean(beanName, bean, beanDefinition)

                    // 将完整的bean加入到单例缓存
                    addSingleton(beanName, exposedObject)

                    return exposedObject
                } finally {
                    afterSingletonCreation(beanName)
                }
            }

            // 如果是单例
            if (beanDefinition.isSingleton()) {
                beforeSingletonCreation(beanName)

                // 创建bean实例
                val bean = createBeanInstance(beanDefinition)

                addSingletonFactory(beanName, object: ObjectFactory<Any> {
                    override fun getObject(): Any {
                        return getEarlyBeanReference(beanName, beanDefinition, bean)
                    }
                })

                try {
                    // 填充属性
                    populateBean(beanName, bean, beanDefinition)
                    // 初始化bean
                    val exposedObject = initializeBean(beanName, bean, beanDefinition)
                    // 将完整的bean加入到单例缓存
                    addSingleton(beanName, exposedObject)

                    return exposedObject
                } finally {
                    afterSingletonCreation(beanName)
                }
            }

            // 如果是prototype,每次都创建新实例
            val bean = createBeanInstance(beanDefinition)
            populateBean(beanName, bean, beanDefinition)
            return initializeBean(beanName, bean, beanDefinition)
        } catch (e: Exception) {
            throw BeansException("Error creating bean with name '$beanName'", e)
        }
    }

    override fun destroySingletons() {
        val singletonNames = getSingletonNames()
        singletonNames.forEach { singletonName ->
            destroySingleton(singletonName)
        }
    }

    /**
     * 验证别名是否有效
     */
    protected fun validateAlias(beanName: String, alias: String) {
        // 检查是否存在循环引用
        if (hasAlias(alias, beanName)) {
            throw BeansException("Cannot register alias '$alias' for bean '$beanName': Circular reference - '$beanName' is already defined as an alias for '$alias'")
        }

        // 检查别名是否已经被使用
        if (containsBean(alias) && beanName != transformedBeanName(alias)) {
            throw BeansException("Cannot register alias '$alias' for bean '$beanName': It's already in use for bean '${transformedBeanName(alias)}'")
        }
    }

    override fun registerAlias(name: String, alias: String) {
        if (alias == name) {
            resolveAlias(alias)
            return
        }
        if (hasAlias(name, alias)) {
            return
        }

        validateAlias(name, alias)
        super.registerAlias(name, alias)
        logger.debug("Registered alias '{}' for bean '{}'", alias, name)
    }

    /**
     * 检查是否存在指定的别名
     */
    protected fun hasAlias(name: String, alias: String): Boolean {
        val aliases = getAliases(name)
        return aliases.contains(alias)
    }

    override fun getBeanFactory(): ConfigurableBeanFactory {
        return this
    }

    override fun <T> createBean(beanClass: Class<T>): T {
        try {
            val instance = beanClass.getDeclaredConstructor().newInstance()
            logger.debug("Created new instance of bean class [{}]", beanClass.name)

            return instance
        } catch (e: Exception) {
            throw BeansException("Error creating bean with class '" + beanClass.name + "'", e)
        }
    }

    override fun configureBean(existingBean: Any, beanName: String): Any {
        var result = existingBean

        // 应用BeanPostProcessor的前置处理
        getBeanPostProcessor().forEach { beanPostProcessor ->
            val current = beanPostProcessor.postProcessBeforeInitialization(result, beanName) ?: return result
            result = current
        }

        // 应用BeanPostProcessor的后置处理
        getBeanPostProcessor().forEach { beanPostProcessor ->
            val current = beanPostProcessor.postProcessAfterInitialization(result, beanName) ?: return result
            result = current
        }

        logger.debug("Configured bean [{}] of type [{}]", beanName, existingBean.javaClass.name)
        return result
    }

    override fun doGetBean(beanName: String): Any {
        val canonicalName = canonicalName(beanName)
        var bean: Any?

        // 检查是否是单例
        if (isSingleton(canonicalName)) {
            // 获取单例
            bean = getSingleton(canonicalName)
            if (bean == null) {
                val beanDefinition = getBeanDefinition(canonicalName)
                bean = createBean(canonicalName, beanDefinition)
                // 添加到单例缓存
                addSingleton(canonicalName, bean)
            }
        } else {
            // 对于prototype，每次都创建新实例
            val beanDefinition = getBeanDefinition(canonicalName)
            bean = createBean(canonicalName, beanDefinition)
        }

        return bean
    }


    override fun autowireBean(existingBean: Any) {
        logger.debug("Autowiring bean of type [{}]", existingBean.javaClass.getName())
    }

    override fun resolveDependency(descriptor: Class<*>, beanName: String): Any {
        TODO("Not yet implemented")
    }

    override fun getBeanPostProcessorCount(): Int {
        return getBeanPostProcessor().size
    }

    override fun containsSingleton(beanName: String): Boolean {
        return this.singletonObjects.containsKey(beanName)
    }

    override fun getSingletonNames(): Array<String> {
        return this.singletonObjects.keys.toTypedArray()
    }

    override fun getSingletonCount(): Int {
        return this.singletonObjects.size
    }

    override fun getAliases(name: String): Array<String> {
        val beanName = transformedBeanName(name)
        val aliases = mutableListOf<String>()

        val directAliases = super.getAliases(beanName)
        if (directAliases.isNotEmpty()) {
            directAliases.forEach { alias ->
                aliases.add(alias)
                // 递归获取别名的别名
                val transitiveAliases = super.getAliases(alias)
                if (transitiveAliases.isNotEmpty()) {
                    transitiveAliases.forEach { transitiveAlias ->
                        if (! aliases.contains(transitiveAlias)) {
                            aliases.add(transitiveAlias)
                        }
                    }
                }
            }
        }

        return aliases.toTypedArray()
    }

    protected fun cleanUpSingletonCache(beanName: String) {
        synchronized(this.singletonObjects) {
            // 从所有缓存中移除
            this.singletonObjects.remove(beanName)
            this.earlySingletonObjects.remove(beanName)
            this.singletonFactories.remove(beanName)
            // 移除合并的bean定义
            this.mergedBeanDefinitions.remove(beanName)
            // 移除所有别名的缓存
            val aliases = getAliases(beanName)
            aliases.forEach {alias ->
                this.singletonObjects.remove(alias)
                this.earlySingletonObjects.remove(alias)
                this.singletonFactories.remove(alias)
                this.mergedBeanDefinitions.remove(alias)
            }
        }
    }

    fun getSingleton(beanName: String, allowEarlyReference: Boolean): Any? {
        // 首先从单例缓存中获取
        var singletonObject = singletonObjects[beanName]
        if (singletonObject == null && isSingletonCurrentlyInCreation(beanName)) {
            // 从早期单例缓存中获取
            singletonObject = this.earlySingletonObjects[beanName]
            if (singletonObject == null && allowEarlyReference) {
                val singletonFactory = this.singletonFactories[beanName]
                if (singletonFactory != null) {
                    singletonObject = singletonFactory.getObject()
                    this.earlySingletonObjects[beanName] = singletonFactory
                    this.singletonFactories.remove(beanName)
                }
            }
        }

        return singletonObject
    }

    /**
     * 获取bean定义持有者
     *
     * @param beanName bean名称
     * @return bean定义持有者
     * @throws BeansException 如果找不到bean定义
     */
    @Throws(BeansException::class)
    fun getBeanDefinitionHolder(beanName: String): BeanDefinitionHolder {
        val beanDefinition = getBeanDefinition(beanName)
        val aliases = getAliases(beanName)
        return BeanDefinitionHolder(beanDefinition, beanName, aliases)
    }

    /**
     * 转换bean名称
     * 处理别名等情况
     */
    override fun transformedBeanName(name: String): String {
        return when {
            containsBeanDefinition(name) -> name
            containsSingleton(name) -> name
            else -> resolveAlias(name)
        }
    }

    /**
     * 解析别名
     * 如果给定的名称是别名，返回对应的bean名称
     * 否则返回原始名称
     */
    private fun resolveAlias(alias: String): String {
        if (! isAlias(alias)) {
            return alias
        }

        val aliases = super.getAliases(alias)
        return if (aliases.isNotEmpty()) aliases[0] else alias
    }

    /**
     * 初始化bean
     */
    protected fun initializeBean(beanName: String, bean: Any, beanDefinition: BeanDefinition): Any {
        // 执行Aware方法
        if (bean is Aware) {
            when (bean) {
                is BeanFactoryAware -> bean.setBeanFactory(this)
                is BeanNameAware -> bean.setBeanName(beanName)
            }
        }
        // 执行BeanPostProcessor的前置处理
        var wrappedBean = applyBeanPostProcessorsBeforeInitialization(bean, beanName)

        // 执行初始化方法
        try {
            invokeInitMethod(beanName, wrappedBean, beanDefinition)
        } catch (e: Exception) {
            throw BeansException("Invocation of init method failed", e)
        }

        wrappedBean = applyBeanPostProcessorAfterInitialization(wrappedBean, beanName)
        return wrappedBean
    }

    /**
     * 执行bean的初始化方法
     */
    @Throws(BeansException::class)
    protected fun invokeInitMethod(beanName: String, bean: Any, beanDefinition: BeanDefinition) {
        // 执行InitializingBean接口的方法
        if (bean is InitializingBean) {
            bean.afterPropertySet()
        }

        // 执行自定义的init-method
        val initMethodName = beanDefinition.getInitMethodName()
        if (! initMethodName.isNullOrBlank()) {
            val initMethod = bean.javaClass.getMethod(initMethodName)
            initMethod.invoke(bean)
        }
    }

    override fun isSingletonCurrentlyInCreation(beanName: String): Boolean {
        return this.singletonCurrentlyInCreation.contains(beanName)
    }

    protected fun destroySingleton(beanName: String) {
        val singletonInstance = getSingleton(beanName)

        if (singletonInstance != null) {
            val beanDefinition = getBeanDefinition(beanName)
            try {
                val destroyMethodName = beanDefinition.getDestroyMethodName()
                if (destroyMethodName != null) {
                    val destroyMethod = singletonInstance.javaClass.getDeclaredMethod(destroyMethodName)
                    destroyMethod.isAccessible = true
                    destroyMethod.invoke(singletonInstance)
                    logger.debug("Invoked destroy method '{}' on bean '{}'",
                        beanDefinition.getDestroyMethodName(), beanName)
                }
            } catch (e: Exception) {
                logger.error("Error invoking destroy method on bean '$beanName'", e)
            }
        }

        this.singletonObjects.remove(beanName)
        logger.debug("Destroyed singleton bean '{}'", beanName)
    }

    @Suppress("UNCHECKED_CAST")
    override fun <T> doGetBean(name: String, requiredType: Class<T>?): T {
        val canonicalName = canonicalName(name)
        var bean: Any? = null

        // 获取bean定义
        val beanDefinition = getBeanDefinition(canonicalName)

        // 只有singleton才尝试从缓存中获取
        if (beanDefinition.isSingleton()) {
            // 先尝试从单例缓存中获取
            bean = getSingleton(canonicalName, true)

            // 如果是单例且正在创建中，尝试从三级缓存中获取早期引用
            if (bean == null && isSingletonCurrentlyInCreation(canonicalName)) {
                bean = getSingleton(canonicalName, false)
                if (bean != null) {
                    logger.debug("Returning early reference for singleton bean '$canonicalName'")
                }
            }
        }

        // 如果没有从缓存中获取到或者是prototype，创建新的实例
        if (bean == null) {
            try {
                bean = createBean(canonicalName, beanDefinition)
            } catch (e: Exception) {
                throw BeansException("Error creating bean '$canonicalName'", e)
            }
        }

        // 类型检查
        if (requiredType != null && !requiredType.isInstance(bean)) {
            throw BeansException(
                "Bean named '$name' is expected to be of type '${requiredType.name}'" +
                        " but was actually of type '${bean.javaClass.name}'"
            )
        }

        return bean as T
    }

}