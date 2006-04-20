<#ftl>
<#macro generateClassHeader componentDescriptor>
  <#local package=componentDescriptor.name[0..componentDescriptor.name?last_index_of(".")-1]/>
  <#local componentName=componentDescriptor.name[componentDescriptor.name?last_index_of(".")+1..]/>
  <#local superInterfaceList=[]/>
  <#if componentDescriptor.ancestorDescriptors?exists>
    <#list componentDescriptor.ancestorDescriptors as ancestorDescriptor>
      <#local superInterfaceList=superInterfaceList + [ancestorDescriptor.name]/>
      <#if instanceof(ancestorDescriptor, "com.d2s.framework.model.descriptor.entity.IEntityDescriptor")>
        <#local superEntity=componentDescriptor/>
      </#if>
    </#list>
  </#if>
  <#if componentDescriptor.serviceContracts?exists>
    <#list componentDescriptor.serviceContracts as serviceContract>
      <#local superInterfaceList=superInterfaceList + [serviceContract.name]/>
    </#list>
  </#if>
  <#local isEntity=instanceof(componentDescriptor, "com.d2s.framework.model.descriptor.entity.IEntityDescriptor")/>
/*
 * Generated by Design2see. All rights reserved.
 */
package ${package};

/**
 * ${componentName} <#if isEntity>entity<#else>component</#if>.
 * <p>
 * Generated by Design2see. All rights reserved.
 * <p>
 * 
  <#if isEntity>
 * @hibernate.mapping
 *           default-access = "com.d2s.framework.model.persistence.hibernate.property.EntityPropertyAccessor"
 *           package = "${package}"
    <#if superEntity?exists>
 * @hibernate.joined-subclass
    <#else>
 * @hibernate.class
    </#if>
 *           table = "${generateSQLName(componentName)}"
 *           dynamic-insert = "true"
 *           dynamic-update = "true"
 *           persister = "com.d2s.framework.model.persistence.hibernate.entity.persister.EntityProxyJoinedSubclassEntityPersister"
    <#if componentDescriptor.purelyAbstract>
 *           abstract = "true"
    </#if>
    <#if superEntity?exists>
 * @hibernate.joined-subclass-key
 *           column = "ID"
    </#if>
  </#if>
 * @author Generated by Design2see
 * @version $LastChangedRevision$
 */
public interface ${componentName}<#if (superInterfaceList?size > 0)> extends
<#list superInterfaceList as superInterface>  ${superInterface}<#if superInterface_has_next>,${"\n"}<#else> {</#if></#list>
<#else> {
</#if>
  <#if isEntity && !superEntity?exists>

  /**
   * @hibernate.id generator-class = "assigned" column = "ID" type = "string"
   *               length = "36"
   * <p>
   * {@inheritDoc}
   */
  java.io.Serializable getId();

  /**
   * @hibernate.version column = "VERSION" unsaved-value = "null"
   * <p>
   * {@inheritDoc}
   */
  Integer getVersion();
  </#if>
  
</#macro>

<#macro generateScalarSetter componentDescriptor propertyDescriptor>
  <#local propertyName=propertyDescriptor.name/>
  <#if propertyDescriptor.propertyClass.array>
    <#local propertyType=propertyDescriptor.propertyClass.componentType.name+"[]"/>
  <#else>
    <#local propertyType=propertyDescriptor.propertyClass.name/>
  </#if>
  /**
   * Sets the ${propertyName}.
   * 
   * @param ${propertyName}
   *          the ${propertyName} to set.
   */
  void set${propertyName?cap_first}(${propertyType} ${propertyName});
</#macro>

<#macro generateScalarGetter componentDescriptor propertyDescriptor>
  <#local propertyName=propertyDescriptor.name/>
  <#if propertyDescriptor.propertyClass.array>
    <#local propertyType=propertyDescriptor.propertyClass.componentType.name+"[]"/>
  <#else>
    <#local propertyType=propertyDescriptor.propertyClass.name/>
  </#if>
  /**
   * Gets the ${propertyName}.
   * 
   <#if !componentDescriptor.computed && !propertyDescriptor.delegateClassName?exists>
   * @hibernate.property
     <#if instanceof(propertyDescriptor, "com.d2s.framework.model.descriptor.IDatePropertyDescriptor")>
       <#if propertyDescriptor.type = "DATE">
   *           type = "date"
       <#elseif propertyDescriptor.type = "TIME">
   *           type = "time"
       <#else>
   *           type = "timestamp"
       </#if>
<#-- <#elseif    instanceof(propertyDescriptor, "com.d2s.framework.model.descriptor.IBinaryPropertyDescriptor")
              && !(propertyDescriptor.maxLength?exists)>
   *           type = "blob"
-->
     </#if>
   * @hibernate.column
   *           name = "${generateSQLName(propertyName)}"
     <#if (   instanceof(propertyDescriptor, "com.d2s.framework.model.descriptor.IStringPropertyDescriptor")
           || instanceof(propertyDescriptor, "com.d2s.framework.model.descriptor.IBinaryPropertyDescriptor")
          )
       && (propertyDescriptor.maxLength?exists)>
   *           length = "${propertyDescriptor.maxLength?c}"
     </#if>
     <#if propertyDescriptor.mandatory>
   *           not-null = "true"
     </#if>
     <#if propertyDescriptor.unicityScope?exists>
   *           unique-key = "${generateSQLName(propertyDescriptor.unicityScope)}_UNQ"
     </#if>
     <#if instanceof(propertyDescriptor, "com.d2s.framework.model.descriptor.INumberPropertyDescriptor")>
       <#if (propertyDescriptor.minValue?exists)
          &&(propertyDescriptor.maxValue?exists)>
         <#local infLength=propertyDescriptor.minValue?int?c?length/>
         <#local supLength=propertyDescriptor.maxValue?int?c?length/>
         <#if (infLength > supLength)>
           <#local length=infLength/>
         <#else>
           <#local length=supLength/>
         </#if>
   *           scale = "${length?c}"
       <#else>
   *           scale = "10"
       </#if>
       <#if instanceof(propertyDescriptor, "com.d2s.framework.model.descriptor.IDecimalPropertyDescriptor")>
         <#if propertyDescriptor.maxFractionDigit?exists>
   *           precision = "${propertyDescriptor.maxFractionDigit?c}"
         <#else>
   *           precision = "2"
         </#if>
       </#if>
     </#if>
   </#if>
   * @return the ${propertyName}.
   */
  ${propertyType} get${propertyName?cap_first}();
</#macro>

<#macro generateCollectionSetter componentDescriptor propertyDescriptor>
  <#local propertyName=propertyDescriptor.name/>
  <#local collectionType=propertyDescriptor.propertyClass.name/>
  <#local elementType=propertyDescriptor.referencedDescriptor.elementDescriptor.name/>
  /**
   * Sets the ${propertyName}.
   * 
   * @param ${propertyName}
   *          the ${propertyName} to set.
   */
  void set${propertyName?cap_first}(${collectionType}<${elementType}> ${propertyName});
</#macro>

<#macro generateCollectionAdder componentDescriptor propertyDescriptor>
  <#local propertyName=propertyDescriptor.name/>
  <#local elementType=propertyDescriptor.referencedDescriptor.elementDescriptor.name/>
  /**
   * Adds an element to the ${propertyName}.
   * 
   * @param ${propertyName}Element
   *          the ${propertyName} element to add.
   */
  void addTo${propertyName?cap_first}(${elementType} ${propertyName}Element);
  <#if propertyDescriptor.propertyClass.name = "java.util.List">

  /**
   * Adds an element to the ${propertyName} at the specified index. If the index is out
   * of the list bounds, the element is simply added at the end of the list.
   * 
   * @param index
   *          the index to add the ${propertyName} element at.
   * @param ${propertyName}Element
   *          the ${propertyName} element to add.
   */
  void addTo${propertyName?cap_first}(int index, ${elementType} ${propertyName}Element);
  </#if>
</#macro>

<#macro generateCollectionRemer componentDescriptor propertyDescriptor>
  <#local propertyName=propertyDescriptor.name/>
  <#local elementType=propertyDescriptor.referencedDescriptor.elementDescriptor.name/>
  /**
   * Removes an element from the ${propertyName}.
   * 
   * @param ${propertyName}Element
   *          the ${propertyName} element to remove.
   */
  void removeFrom${propertyName?cap_first}(${elementType} ${propertyName}Element);
</#macro>

<#macro generateCollectionGetter componentDescriptor propertyDescriptor>
  <#local propertyName=propertyDescriptor.name/>
  <#local collectionType=propertyDescriptor.propertyClass.name/>
  <#local elementDescriptor=propertyDescriptor.referencedDescriptor.elementDescriptor/>
  <#local elementType=propertyDescriptor.referencedDescriptor.elementDescriptor.name/>
  <#local componentName=componentDescriptor.name[componentDescriptor.name?last_index_of(".")+1..]/>
  <#local elementName=elementType[elementType?last_index_of(".")+1..]/>
  <#if collectionType="java.util.List">
    <#local hibernateCollectionType="list"/>
  <#elseif collectionType="java.util.Set">
    <#local hibernateCollectionType="set"/>
  </#if>
  <#local manyToMany=propertyDescriptor.manyToMany/>
  <#if propertyDescriptor.reverseRelationEnd?exists>
    <#local bidirectional=true/>
    <#local reversePropertyName=propertyDescriptor.reverseRelationEnd.name/>
    <#if manyToMany>
      <#local inverse=(compareStrings(elementName, componentName) > 0)/>
    <#else>
      <#if hibernateCollectionType="list">
        <#local inverse=false/>
      <#else>
        <#local inverse=true/>
      </#if>
    </#if>
  <#else>
    <#local bidirectional=false/>
    <#local inverse=false/>
    <#if !manyToMany>
      <#local reversePropertyName=propertyDescriptor.name+"Parent"/>
    </#if>
  </#if>
  /**
   * Gets the ${propertyName}.
   * 
   <#if !componentDescriptor.computed && !propertyDescriptor.delegateClassName?exists>
   * @hibernate.${hibernateCollectionType}
     <#if manyToMany && inverse>
   *           cascade = "none"
       <#else>
         <#if propertyDescriptor.composition>
   *           cascade = "persist,merge,save-update,refresh,evict,replicate,delete"
         <#else>
   *           cascade = "persist,merge,save-update,refresh,evict,replicate"
         </#if>
       </#if>
     <#if manyToMany>
       <#if inverse>
   *           table = "${generateSQLName(elementName)}_${generateSQLName(reversePropertyName)}"
       <#else>
   *           table = "${generateSQLName(componentName)}_${generateSQLName(propertyName)}"
       </#if>
     </#if>
     <#if inverse>
   *           inverse = "true"
     </#if>
<#--
     <#if (propertyDescriptor.orderingProperties?exists && !manyToMany && !(hibernateCollectionType="list"))>
   *           order-by="<#list propertyDescriptor.orderingProperties as orderingProperty>${generateSQLName(orderingProperty)}<#if orderingProperty_has_next>,</#if></#list>"
     </#if>
-->
<#--
  The following replaces the previous block wich makes hibernate fail... Ordering is now handled in the entity itself.
  But hibernate must be provided with an ordering attribute so that a Linked HashSet is used instead of a set.
-->
     <#if (propertyDescriptor.orderingProperties?exists && !manyToMany && !(hibernateCollectionType="list"))>
   *           order-by="ID"
     </#if>
     <#if manyToMany>
   * @hibernate.key
       <#if componentName=elementName>
         <#if inverse>
   *           column = "${generateSQLName(componentName)}_ID2"
         <#else>
   *           column = "${generateSQLName(componentName)}_ID1"
         </#if>
       <#else>
   *           column = "${generateSQLName(componentName)}_ID"
       </#if>
   * @hibernate.many-to-many
   *           class = "${elementType}"
       <#if componentName=elementName>
         <#if inverse>
   *           column = "${generateSQLName(elementName)}_ID1"
         <#else>
   *           column = "${generateSQLName(elementName)}_ID2"
         </#if>
       <#else>
   *           column = "${generateSQLName(elementName)}_ID"
       </#if>
     <#else>
   * @hibernate.key
   *           column = "${generateSQLName(reversePropertyName)}_ID"
   * @hibernate.one-to-many
   *           class = "${elementType}"
     </#if>
     <#if hibernateCollectionType="list">
   * @hibernate.list-index
   *           column = "${generateSQLName(propertyName)}_SEQ"
     </#if>
   </#if>
   * @return the ${propertyName}.
   */
  <#if generateAnnotations>
  @com.d2s.framework.util.bean.ElementClass(${elementType}.class)
  </#if>
  ${collectionType}<${elementType}> get${propertyName?cap_first}();
</#macro>

<#macro generateEntityRefSetter componentDescriptor propertyDescriptor>
  <#local propertyName=propertyDescriptor.name/>
  <#local propertyType=propertyDescriptor.referencedDescriptor.name/>
  /**
   * Sets the ${propertyName}.
   * 
   * @param ${propertyName}
   *          the ${propertyName} to set.
   */
  void set${propertyName?cap_first}(${propertyType} ${propertyName});
</#macro>

<#macro generateEntityRefGetter componentDescriptor propertyDescriptor>
  <#local propertyName=propertyDescriptor.name/>
  <#local propertyType=propertyDescriptor.referencedDescriptor.name/>
  <#local isEntity=instanceof(propertyDescriptor.referencedDescriptor, "com.d2s.framework.model.descriptor.entity.IEntityDescriptor")/>
  <#if propertyDescriptor.reverseRelationEnd?exists>
    <#local bidirectional=true/>
    <#if instanceof(propertyDescriptor.reverseRelationEnd, "com.d2s.framework.model.descriptor.IReferencePropertyDescriptor")>
      <#local oneToOne=true/>
      <#local componentName=componentDescriptor.name[componentDescriptor.name?last_index_of(".")+1..]/>
      <#local elementName=propertyType[propertyType?last_index_of(".")+1..]/>
      <#local reverseOneToOne=(compareStrings(elementName, componentName) < 0)/>
    <#else>
      <#local oneToOne=false/>
      <#local reverseOneToOne=false/>
      <#if propertyDescriptor.reverseRelationEnd.propertyClass.name="java.util.List">
        <#local managesPersistence=false/>
      <#else>
        <#local managesPersistence=true/>
      </#if>
    </#if>
  <#else>
    <#local bidirectional=false/>
    <#local oneToOne=false/>
    <#local reverseOneToOne=false/>
  </#if>
  /**
   * Gets the ${propertyName}.
   * 
  <#if !componentDescriptor.computed && !propertyDescriptor.delegateClassName?exists>
    <#if isEntity>
      <#if reverseOneToOne>
   * @hibernate.one-to-one 
   *           cascade = "persist,merge,save-update,refresh,evict,replicate"
   *           property-ref = "${propertyDescriptor.reverseRelationEnd.name}"
      <#else>
   * @hibernate.many-to-one 
        <#if oneToOne>
   *           cascade = "persist,merge,save-update,refresh,evict,replicate"
        <#elseif bidirectional>
   *           cascade = "persist,merge,save-update"
          <#if !managesPersistence>
   *           insert = "false"
   *           update = "false"
          </#if>
        <#else>
   *           cascade = "none"
        </#if>
   * @hibernate.column
   *           name = "${generateSQLName(propertyName)}_ID"
        <#if oneToOne>
   *           unique = "true"
        </#if>
        <#if propertyDescriptor.mandatory>
   *           not-null = "true"
        </#if>
        <#if propertyDescriptor.unicityScope?exists>
   *           unique-key = "${generateSQLName(propertyDescriptor.unicityScope)}_UNQ"
        </#if>
      </#if>
    <#else>
   * @hibernate.any
   *           id-type = "string"
   * @hibernate.any-column
   *           name = "${generateSQLName(propertyName)}_NAME"
   * @hibernate.any-column
   *           name = "${generateSQLName(propertyName)}_ID"
      <#if oneToOne>
   *           unique = "true"
      </#if>
      <#if propertyDescriptor.mandatory>
   *           not-null = "true"
      </#if>
      <#if propertyDescriptor.unicityScope?exists>
   *           unique-key = "${generateSQLName(propertyDescriptor.unicityScope)}_UNQ"
      </#if>
    </#if>
  </#if>
   * @return the ${propertyName}.
   */
  ${propertyType} get${propertyName?cap_first}();
</#macro>

<#macro generateCollectionPropertyAccessors componentDescriptor propertyDescriptor>
  <@generateCollectionGetter componentDescriptor=componentDescriptor propertyDescriptor=propertyDescriptor/>

  <#if !propertyDescriptor.readOnly>
    <@generateCollectionSetter componentDescriptor=componentDescriptor propertyDescriptor=propertyDescriptor/>

    <@generateCollectionAdder componentDescriptor=componentDescriptor propertyDescriptor=propertyDescriptor/>

    <@generateCollectionRemer componentDescriptor=componentDescriptor propertyDescriptor=propertyDescriptor/>

  </#if>
</#macro>

<#macro generateReferencePropertyAccessors componentDescriptor propertyDescriptor>
  <@generateEntityRefGetter componentDescriptor=componentDescriptor propertyDescriptor=propertyDescriptor/>

  <#if !propertyDescriptor.readOnly>
    <@generateEntityRefSetter componentDescriptor=componentDescriptor propertyDescriptor=propertyDescriptor/>

  </#if>
</#macro>

<#macro generateScalarPropertyAccessors componentDescriptor propertyDescriptor>
  <@generateScalarGetter componentDescriptor=componentDescriptor propertyDescriptor=propertyDescriptor/>

  <#if !propertyDescriptor.readOnly>
    <@generateScalarSetter componentDescriptor=componentDescriptor propertyDescriptor=propertyDescriptor/>

  </#if>
</#macro>

<#macro generatePropertyAccessors componentDescriptor propertyDescriptor>
  <#if instanceof(propertyDescriptor, "com.d2s.framework.model.descriptor.ICollectionPropertyDescriptor")>
    <@generateCollectionPropertyAccessors componentDescriptor=componentDescriptor propertyDescriptor=propertyDescriptor/>
  <#elseif instanceof(propertyDescriptor, "com.d2s.framework.model.descriptor.IReferencePropertyDescriptor")>
    <@generateReferencePropertyAccessors componentDescriptor=componentDescriptor propertyDescriptor=propertyDescriptor/>
  <#else>
    <@generateScalarPropertyAccessors componentDescriptor=componentDescriptor propertyDescriptor=propertyDescriptor/>
  </#if>
</#macro>
<@generateClassHeader componentDescriptor=componentDescriptor/>
<#if componentDescriptor.declaredPropertyDescriptors?exists>
  <#list componentDescriptor.declaredPropertyDescriptors as propertyDescriptor>
    <@generatePropertyAccessors componentDescriptor=componentDescriptor propertyDescriptor=propertyDescriptor/>
  </#list>
<#else>
  // THIS IS JUST A MARKER INTERFACE.
</#if>
}
