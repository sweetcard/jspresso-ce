<#macro generatePackageHeader componentDescriptor>
  <#local componentName=componentDescriptor.name[componentDescriptor.name?last_index_of(".")+1..]/>
  <#local package=componentDescriptor.name[0..componentDescriptor.name?last_index_of(".")-1]/>
/*
 * Generated by Jspresso. All rights reserved.
 */
package ${package};
  <#if componentDescriptor.sqlName??>
    <#global tableName=componentDescriptor.sqlName/>
    <#global documentName=componentDescriptor.sqlName/>
  <#else>
    <#global tableName=generateSQLName(componentName)/>
  </#if>
  <#global reducedTableName=reduceSQLName(tableName)/>
</#macro>
<#macro generateClassHeader componentDescriptor translationInnerClass>
  <#local componentName=componentDescriptor.name[componentDescriptor.name?last_index_of(".")+1..]/>
  <#local superInterfaceList=[]/>
  <#global isEntity=componentDescriptor.entity/>
  <#if componentDescriptor.ancestorDescriptors??>
    <#list componentDescriptor.ancestorDescriptors as ancestorDescriptor>
      <#if "org.jspresso.framework.model.entity.IEntity" != ancestorDescriptor.name>
	      <#local superInterfaceList=superInterfaceList + [ancestorDescriptor.name]/>
	      <#if ancestorDescriptor.entity>
	        <#local superEntity=ancestorDescriptor/>
	        <#if superEntity.sqlName??>
	          <#local superEntityTableName=superEntity.sqlName/>
	        <#else>
	          <#local superEntityName=superEntity.name[superEntity.name?last_index_of(".")+1..]/>
	          <#local superEntityTableName=generateSQLName(superEntityName)/>
	        </#if>
	        <#local idDescriptor = componentDescriptor.getPropertyDescriptor("id")/>
	        <#if idDescriptor.sqlName??>
            <#local idColumnName=idDescriptor.sqlName/>
	        <#else>
	          <#local idColumnName=generateSQLName(idDescriptor.name)/>
	        </#if>
	      </#if>
	    </#if>
    </#list>
    <#if isEntity && !(superEntity??)>
      <#local superInterfaceList = ["org.jspresso.framework.model.entity.IEntity"] + superInterfaceList/>
    </#if>
  </#if>
  <#if !translationInnerClass>
    <#local superInterfaceList = ["I" + componentName + "Extension"] + superInterfaceList/>
  </#if>
  <#if componentDescriptor.serviceContractClassNames??>
    <#list componentDescriptor.serviceContractClassNames as serviceContractClassName>
      <#local superInterfaceList=superInterfaceList + [serviceContractClassName]/>
    </#list>
  </#if>
/**
 * ${componentName} <#if isEntity>entity<#else>component</#if>.
 * <p>
 * Generated by Jspresso. All rights reserved.
 * <p>
 *
  <#if isEntity>
 * @hibernate.mapping
 *           default-access = "org.jspresso.framework.model.persistence.hibernate.property.EntityPropertyAccessor"
 *           default-cascade="persist,merge,save-update"
    <#if superEntity??>
 * @hibernate.joined-subclass
    <#else>
 * @hibernate.class
    </#if>
 *           table = "${reducedTableName}"
 *           dynamic-insert = "true"
 *           dynamic-update = "true"
 *           persister =
 *            "org.jspresso.framework.model.persistence.hibernate.entity.persister.EntityProxyJoinedSubclassEntityPersister"
    <#if componentDescriptor.purelyAbstract>
 *           abstract = "true"
    </#if>
    <#if superEntity??>
 * @hibernate.joined-subclass-key
 *           column = "${reduceSQLName(idColumnName)}"
    </#if>
  </#if>
 * @author Generated by Jspresso
 */
@SuppressWarnings("all")
<#if isEntity && documentName??>
@org.springframework.data.mongodb.core.mapping.Document(collection="${documentName}")
</#if>
public interface ${componentName}<#if (superInterfaceList?size > 0)> extends
<#list superInterfaceList as superInterface>  ${superInterface?replace("$", ".")}<#if superInterface_has_next>,${"\n"}<#else> {</#if></#list>
<#else> {
</#if>
  <#if !componentDescriptor.purelyAbstract>
    <@generateStateInnerClass componentDescriptor=componentDescriptor/>
  </#if>
  <#if isEntity>

  /**
   * Table name used for storing the entity.
   */
  String TABLE = "${reducedTableName}";
    <#if !superEntity??>
      <@generateScalarGetter componentDescriptor=componentDescriptor propertyDescriptor=componentDescriptor.getPropertyDescriptor("id") overridden=false/>
      <@generateScalarGetter componentDescriptor=componentDescriptor propertyDescriptor=componentDescriptor.getPropertyDescriptor("version") overridden=false/>
    </#if>
  </#if>

</#macro>

<#macro generateStateInnerClass componentDescriptor>

  /**
   * The State innerclass.
   */
  static class State extends org.jspresso.framework.model.component.basic.BasicComponentPropertyStore {

  <@generateStateGet componentDescriptor=componentDescriptor/>
  <@generateStateSet componentDescriptor=componentDescriptor/>
  <#list componentDescriptor.propertyDescriptors as propertyDescriptor>
    <#if !propertyDescriptor.computed || propertyDescriptor.persistenceFormula??>
      <@generateStateProperty componentDescriptor=componentDescriptor propertyDescriptor=propertyDescriptor/>
    </#if>
  </#list>
  }
</#macro>

<#macro generateStateGet componentDescriptor>
    /**
     * {@inheritDoc}
     */
    public Object get(String propertyName) {
      switch(propertyName) {
  <#local componentName=componentDescriptor.name[componentDescriptor.name?last_index_of(".")+1..]/>
  <#list componentDescriptor.propertyDescriptors as propertyDescriptor>
    <#if !propertyDescriptor.computed || propertyDescriptor.persistenceFormula??>
      <#local propertyName=propertyDescriptor.name/>
        case <#--<#if propertyDescriptor.computed && propertyDescriptor.persistenceFormula??>${componentName}Extension.</#if>-->${generateSQLName(propertyName)}:
          return get${propertyName?cap_first}();
    </#if>
  </#list>
        default:
          throw new org.jspresso.framework.model.component.ComponentException(
            "Can not read component property " + propertyName + " on " + getClass().getName());
      }
    }

</#macro>

<#macro generateStateSet componentDescriptor>
    /**
     * {@inheritDoc}
     */
    public void set(String propertyName, Object propertyValue) {
      switch(propertyName) {
  <#local componentName=componentDescriptor.name[componentDescriptor.name?last_index_of(".")+1..]/>
  <#list componentDescriptor.propertyDescriptors as propertyDescriptor>
    <#if !propertyDescriptor.computed || propertyDescriptor.persistenceFormula??>
      <#local propertyName=propertyDescriptor.name/>
      <#local propertyType=propertyDescriptor.modelTypeName/>
        case <#--<#if propertyDescriptor.computed && propertyDescriptor.persistenceFormula??>${componentName}Extension.</#if>-->${generateSQLName(propertyName)}:
          set${propertyName?cap_first}((${propertyType}) propertyValue);
          break;
    </#if>
  </#list>
        default:
          throw new org.jspresso.framework.model.component.ComponentException(
            "Can not write component property " + propertyName + " on " + getClass().getName());
      }
    }

</#macro>

<#macro generateStateProperty componentDescriptor propertyDescriptor>
  <#local propertyName=propertyDescriptor.name/>
  <#local propertyType=propertyDescriptor.modelTypeName/>


    /**
     * The ${propertyName} property.
     */
    private ${propertyType} ${propertyName};

    /**
     * The ${propertyName} setter.
     * @param ${propertyName} the ${propertyName} value to set
     */
    public void set${propertyName?cap_first}(${propertyType} ${propertyName}) {
      this.${propertyName} = ${propertyName};
    }

    /**
     * The ${propertyName} getter.
     * @return the ${propertyName} value
     */
    public ${propertyType} get${propertyName?cap_first}() {
      return this.${propertyName};
    }

</#macro>

<#macro generateScalarSetter componentDescriptor propertyDescriptor>
  <#local propertyName=propertyDescriptor.name/>
  <#local propertyType=propertyDescriptor.modelTypeName/>
  /**
   * Sets the ${propertyName}.
   *
   * @param ${propertyName}
   *          the ${propertyName} to set.
   */
  void set${propertyName?cap_first}(${propertyType} ${propertyName});

</#macro>

<#macro generateScalarGetter componentDescriptor propertyDescriptor overridden>
  <#local propertyName=propertyDescriptor.name/>
  <#if propertyDescriptor.name ="id">
    <#local propertyType="java.io.Serializable"/>
  <#else>
    <#local propertyType=propertyDescriptor.modelTypeName/>
  </#if>
  <#if propertyDescriptor.sqlName??>
    <#local columnName=propertyDescriptor.sqlName/>
    <#local columnNameGenerated = false/>
    <#local fieldName=propertyDescriptor.sqlName/>
  <#else>
    <#local columnName=generateSQLName(propertyName)/>
    <#local columnNameGenerated = true/>
  </#if>
  <#if !propertyDescriptor.computed && !overridden>
  /**
   * Column name used to store the ${propertyName} property.
   */
  String ${generateSQLName(propertyName)}_COL = "${reduceSQLName(columnName)}";
  </#if>
  /**
   * Gets the ${propertyName}.
   *
  <#if !propertyDescriptor.computed>
    <#if propertyDescriptor.name ="id">
   * @hibernate.id
   *           generator-class = "assigned"
      <#if hibernateTypeRegistry.getRegisteredType(propertyDescriptor.modelTypeName)??>
        <#if instanceof(propertyDescriptor, "org.jspresso.framework.model.descriptor.IBinaryPropertyDescriptor")>
          <#assign idTypeName="org.jspresso.framework.model.persistence.hibernate.entity.type.ByteArrayType"/>
        <#else>
          <#local hibernateType=hibernateTypeRegistry.getRegisteredType(propertyDescriptor.modelTypeName)/>
          <#if hibernateType??>
            <#assign idTypeName=hibernateType.name/>
          </#if>
        </#if>
        <#local hibernateTypeName=idTypeName/>
      </#if>
      <#if hibernateTypeName??>
   *           type = "${hibernateTypeName}"
      <#else>
   *           type = "string"
      </#if>
    <#elseif propertyDescriptor.name ="version">
   * @hibernate.version
   *           unsaved-value = "null"
   *           column = "${reduceSQLName(columnName)}"
   *           type = "integer"
    <#else>
   * @hibernate.property
    </#if>
    <#if !propertyDescriptor.versionControl>
   *           optimistic-lock = "false"
    </#if>
    <#if instanceof(propertyDescriptor, "org.jspresso.framework.model.descriptor.IDatePropertyDescriptor")>
      <#if propertyDescriptor.type = "DATE_TIME">
   *           type = "timestamp"
      <#else>
   *           type = "date"
      </#if>
    <#elseif instanceof(propertyDescriptor, "org.jspresso.framework.model.descriptor.ITimePropertyDescriptor")>
   *           type = "time"
<#-- <#elseif    instanceof(propertyDescriptor, "org.jspresso.framework.model.descriptor.IBinaryPropertyDescriptor")
              && !(propertyDescriptor.maxLength?exists)>
   *           type = "blob"
-->
    </#if>
   * @hibernate.column
   *           name = "${reduceSQLName(columnName)}"
    <#if (   instanceof(propertyDescriptor, "org.jspresso.framework.model.descriptor.IStringPropertyDescriptor")
          || instanceof(propertyDescriptor, "org.jspresso.framework.model.descriptor.IEnumerationPropertyDescriptor")
          || instanceof(propertyDescriptor, "org.jspresso.framework.model.descriptor.IBinaryPropertyDescriptor")
         )
      && (propertyDescriptor.maxLength??)>
      <#if propertyDescriptor.name ="id">
        <#assign idTypeLength = propertyDescriptor.maxLength>
      </#if>
   *           length = "${propertyDescriptor.maxLength?c}"
    <#elseif instanceof(propertyDescriptor, "org.jspresso.framework.model.descriptor.IColorPropertyDescriptor")>
   *           length = "10"
    </#if>
    <#if (   propertyDescriptor.mandatory
          || instanceof(propertyDescriptor, "org.jspresso.framework.model.descriptor.IBooleanPropertyDescriptor"))>
   *           not-null = "true"
    </#if>
    <#if propertyDescriptor.unicityScope??>
   *           unique-key = "${reduceSQLName(generateSQLName(propertyDescriptor.unicityScope),"_UNQ")}"
    </#if>
    <#if instanceof(propertyDescriptor, "org.jspresso.framework.model.descriptor.INumberPropertyDescriptor")>
      <#local precision = 10>
      <#local scale = 0>
      <#if (propertyDescriptor.minValue??)
         &&(propertyDescriptor.maxValue??)>
        <#local infLength=propertyDescriptor.minValue?c?length/>
        <#local supLength=propertyDescriptor.maxValue?c?length/>
        <#if (infLength > supLength)>
          <#local precision = infLength/>
        <#else>
          <#local precision = supLength/>
        </#if>
        <#if (precision > 32)>
          <#local precision = 32/>
        </#if>
      </#if>
      <#if instanceof(propertyDescriptor, "org.jspresso.framework.model.descriptor.IDecimalPropertyDescriptor")>
        <#if propertyDescriptor.maxFractionDigit??>
          <#local scale = propertyDescriptor.maxFractionDigit/>
        <#else>
          <#local scale = 2/>
        </#if>
      </#if>
   *           precision = "${(precision + scale)?c}"
      <#if (scale > 0)>
   *           scale = "${scale?c}"
      </#if>
    </#if>
  <#elseif propertyDescriptor.sqlName??>
   * @hibernate.property
   *            formula = "${propertyDescriptor.sqlName?replace("{tableName}", reducedTableName)?replace("{entityName}", componentDescriptor.name)}"
  </#if>
   * @return the ${propertyName}.
   */
  <#if instanceof(propertyDescriptor, "org.jspresso.framework.model.descriptor.IBooleanPropertyDescriptor")>
  ${propertyType} is${propertyName?cap_first}();
  <#else>
  <#if propertyDescriptor.name ="version">
  @org.springframework.data.annotation.Version
  </#if>
  <#if !propertyDescriptor.computed && fieldName??>
  @org.springframework.data.mongodb.core.mapping.Field("${fieldName}")
  </#if>
  ${propertyType} get${propertyName?cap_first}();
  </#if>

</#macro>

<#macro generateCollectionSetter componentDescriptor propertyDescriptor>
  <#local propertyName=propertyDescriptor.name/>
  <#local collectionType=propertyDescriptor.modelTypeName/>
  /**
   * Sets the ${propertyName}.
   *
   * @param ${propertyName}
   *          the ${propertyName} to set.
   */
  void set${propertyName?cap_first}(${collectionType} ${propertyName});

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
  void addTo${propertyName?cap_first}(${elementType?replace("$", ".")} ${propertyName}Element);
  <#if propertyDescriptor.modelTypeName?starts_with("java.util.List")>

  /**
   * Adds an element to the ${propertyName} at the specified index. If the index is out
   * of the list bounds, the element is simply added at the end of the list.
   *
   * @param index
   *          the index to add the ${propertyName} element at.
   * @param ${propertyName}Element
   *          the ${propertyName} element to add.
   */
  void addTo${propertyName?cap_first}(int index, ${elementType?replace("$", ".")} ${propertyName}Element);
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
  void removeFrom${propertyName?cap_first}(${elementType?replace("$", ".")} ${propertyName}Element);

</#macro>

<#macro generateCollectionGetter componentDescriptor propertyDescriptor>
  <#local propertyName=propertyDescriptor.name/>
  <#if propertyDescriptor.fkName??>
    <#local fkName=propertyDescriptor.fkName/>
  </#if>
  <#local collectionType=propertyDescriptor.modelTypeName/>
  <#local elementDescriptor=propertyDescriptor.referencedDescriptor.elementDescriptor/>
  <#local elementIsEntity=elementDescriptor.entity/>
  <#local elementType=propertyDescriptor.referencedDescriptor.elementDescriptor.name/>
  <#local componentName=componentDescriptor.name[componentDescriptor.name?last_index_of(".")+1..]/>
  <#local elementName=elementType[elementType?last_index_of(".")+1..]/>
  <#local isEntity=componentDescriptor.entity/>
  <#local isElementEntity=elementDescriptor.entity/>
  <#if collectionType?starts_with("java.util.List")>
    <#local hibernateCollectionType="list"/>
  <#elseif collectionType?starts_with("java.util.Set")>
    <#local hibernateCollectionType="set"/>
  </#if>
  <#local manyToMany=propertyDescriptor.manyToMany/>
  <#if propertyDescriptor.reverseRelationEnd??>
    <#local bidirectional=true/>
    <#local reversePropertyName=propertyDescriptor.reverseRelationEnd.name/>
    <#local reverseMandatory=propertyDescriptor.reverseRelationEnd.mandatory/>
    <#if propertyDescriptor.reverseRelationEnd.fkName??>
      <#local reverseFkName=propertyDescriptor.reverseRelationEnd.fkName/>
    </#if>
    <#if manyToMany>
      <#--
      <#if (compareStrings(elementName, componentName) != 0)>
        <#local inverse=(compareStrings(elementName, componentName) > 0)/>
      <#else>
        Reflexive many to many
        <#local inverse=(compareStrings(propertyName, reversePropertyName) > 0)/>
      </#if>
      -->
      <#local inverse = !propertyDescriptor.leadingPersistence/>
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
      <#local reversePropertyName=propertyName+componentName/>
    </#if>
  </#if>
  <#if componentDescriptor.sqlName??>
    <#local compSqlName=componentDescriptor.sqlName/>
  <#else>
    <#local compSqlName=generateSQLName(componentName)/>
  </#if>
  <#if elementDescriptor.sqlName??>
    <#local eltSqlName=elementDescriptor.sqlName/>
  <#else>
    <#local eltSqlName=generateSQLName(elementName)/>
  </#if>
  <#if propertyDescriptor.sqlName??>
    <#local propSqlName=propertyDescriptor.sqlName/>
    <#local propSqlNameGenerated = false/>
    <#local fieldName=propertyDescriptor.sqlName/>
  <#else>
    <#local propSqlName=generateSQLName(propertyName)/>
    <#local propSqlNameGenerated = true/>
  </#if>
  <#local revSqlNameGenerated = true/>
  <#if propertyDescriptor.reverseRelationEnd??>
	  <#if propertyDescriptor.reverseRelationEnd.sqlName??>
	    <#local revSqlName=propertyDescriptor.reverseRelationEnd.sqlName/>
        <#local revSqlNameGenerated = false/>
	  <#else>
	    <#local revSqlName=generateSQLName(reversePropertyName)/>
	  </#if>
  <#elseif propSqlNameGenerated || !elementIsEntity>
    <#local revSqlName=propSqlName+"_"+compSqlName/>
  <#else>
    <#local revSqlName=propSqlName/>
    <#local revSqlNameGenerated = false/>
	</#if>
  /**
   * Gets the ${propertyName}.
   *
  <#if !propertyDescriptor.computed>
   * @hibernate.${hibernateCollectionType}
    <#if !elementIsEntity>
   *           table = "${compSqlName+"_"+propSqlName}"
    </#if>
    <#if propertyDescriptor.fetchType??>
      <#if propertyDescriptor.fetchType.toString() = "JOIN">
   *           fetch = "join"
      <#elseif propertyDescriptor.fetchType.toString() = "SUBSELECT">
   *           fetch = "subselect"
      </#if>
    </#if>
    <#if propertyDescriptor.batchSize??>
   *           batch-size = "${propertyDescriptor.batchSize?c}"
    </#if>
    <#if !propertyDescriptor.versionControl>
   *           optimistic-lock = "false"
    </#if>
    <#if propertyDescriptor.composition>
   *           cascade = "persist,merge,save-update,delete"
    </#if>
    <#if manyToMany>
      <#if inverse>
        <#local joinTableName=eltSqlName+"_"+revSqlName/>
   *           table = "${reduceSQLName(joinTableName)}"
      <#else>
        <#local joinTableName=compSqlName+"_"+propSqlName/>
   *           table = "${reduceSQLName(joinTableName)}"
      </#if>
      <#--<#local dedupAliasPrefix=compactString(joinTableName)/>-->
      <#local dedupAliasPrefix=""/>
    </#if>
    <#if inverse>
   *           inverse = "true"
    </#if>
<#--
  The following replaces the previous block which makes hibernate fail... Ordering is now handled in the entity itself.
  But hibernate must be provided with an ordering attribute so that a Linked HashSet is used instead of a set but if
  and only if the referenced collection contains entities.
  Well, the following is bad. It kills performance on joined criteria query and it get us into the Hibernate bug
  HHH-7116 and HHH-7630.
    <#if (elementIsEntity && !manyToMany && !(hibernateCollectionType="list"))>
   *           order-by="ID"
    </#if>
-->
    <#if manyToMany>
   * @hibernate.key
      <#if componentName=elementName>
        <#if inverse>
   *           column = "${reduceSQLName("C2"+dedupAliasPrefix+compSqlName,"_ID2")}"
          <#if fkName??>
   *           foreign-key = "${reduceSQLName(dedupSQLName(fkName))}"
          <#else>
   *           foreign-key = "${reduceSQLName(dedupSQLName(joinTableName+"_"+compSqlName),"_FK2")}"
          </#if>
        <#else>
   *           column = "${reduceSQLName("C1"+dedupAliasPrefix+compSqlName,"_ID1")}"
          <#if fkName??>
   *           foreign-key = "${reduceSQLName(dedupSQLName(fkName))}"
          <#else>
   *           foreign-key = "${reduceSQLName(dedupSQLName(joinTableName+"_"+compSqlName),"_FK1")}"
          </#if>
        </#if>
      <#else>
   *           column = "${reduceSQLName(dedupAliasPrefix + compSqlName,"_ID")}"
        <#if fkName??>
   *           foreign-key = "${reduceSQLName(dedupSQLName(fkName))}"
        <#else>
   *           foreign-key = "${reduceSQLName(dedupSQLName(joinTableName+"_"+compSqlName),"_FK")}"
        </#if>
      </#if>
   * @hibernate.many-to-many
   *           class = "${elementType}"
      <#if componentName=elementName>
        <#if inverse>
   *           column = "${reduceSQLName("C1"+dedupAliasPrefix+eltSqlName,"_ID1")}"
          <#if reverseFkName??>
   *           foreign-key = "${reduceSQLName(dedupSQLName(reverseFkName))}"
          <#else>
   *           foreign-key = "${reduceSQLName(dedupSQLName(joinTableName+"_"+eltSqlName),"_FK1")}"
          </#if>
        <#else>
   *           column = "${reduceSQLName("C2"+dedupAliasPrefix+eltSqlName,"_ID2")}"
          <#if reverseFkName??>
   *           foreign-key = "${reduceSQLName(dedupSQLName(reverseFkName))}"
          <#else>
   *           foreign-key = "${reduceSQLName(dedupSQLName(joinTableName+"_"+eltSqlName),"_FK2")}"
          </#if>
        </#if>
      <#else>
   *           column = "${reduceSQLName(dedupAliasPrefix+eltSqlName,"_ID")}"
        <#if reverseFkName??>
   *           foreign-key = "${reduceSQLName(dedupSQLName(reverseFkName))}"
        <#else>
   *           foreign-key = "${reduceSQLName(dedupSQLName(joinTableName+"_"+eltSqlName),"_FK")}"
        </#if>
      </#if>
    <#else>
   * @hibernate.key
      <#if revSqlNameGenerated>
   *           column = "${reduceSQLName(revSqlName,"_ID")}"
      <#else>
   *           column = "${reduceSQLName(revSqlName)}"
      </#if>
      <#if bidirectional>
        <#if reverseMandatory>
   *           not-null = "true"
        </#if>
      </#if>
      <#if isEntity>
        <#if bidirectional>
          <#if fkName??>
   *           foreign-key = "${reduceSQLName(dedupSQLName(fkName))}"
          </#if>
        <#else>
          <#if fkName??>
   *           foreign-key = "${reduceSQLName(dedupSQLName(fkName))}"
          <#else>
   *           foreign-key = "${reduceSQLName(dedupSQLName(revSqlName),"_FK")}"
          </#if>
        </#if>
      <#else>
   *           foreign-key = "none"
      </#if>
      <#if isElementEntity>
   * @hibernate.one-to-many
   *           class = "${elementType}"
      <#else>
   * @hibernate.composite-element
   *           class = "${elementType}"
      </#if>
    </#if>
    <#if hibernateCollectionType="list">
   * @hibernate.list-index
      <#if propSqlNameGenerated || !elementIsEntity>
   *           column = "${reduceSQLName(compSqlName+"_"+propSqlName,"_SEQ")}"
      <#else>
   *           column = "${reduceSQLName(propSqlName,"_SEQ")}"
      </#if>
    </#if>
  </#if>
   * @return the ${propertyName}.
   */
  <#if generateAnnotations>
  @org.jspresso.framework.util.bean.ElementClass(${elementType}.class)
  </#if>
  <#if !propertyDescriptor.computed && fieldName??>
  @org.springframework.data.mongodb.core.mapping.Field("${fieldName}")
  </#if>
  ${collectionType} get${propertyName?cap_first}();

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
  void set${propertyName?cap_first}(${propertyType?replace("$", ".")} ${propertyName});

</#macro>

<#macro generateComponentRefGetter componentDescriptor propertyDescriptor>
  <#local propertyName=propertyDescriptor.name/>
  <#if propertyDescriptor.fkName??>
    <#local fkName=propertyDescriptor.fkName/>
  </#if>
  <#local propertyType=propertyDescriptor.referencedDescriptor.name/>
  <#if propertyDescriptor.referencedDescriptor.sqlName??>
    <#local refSqlName=propertyDescriptor.referencedDescriptor.sqlName/>
  <#else>
    <#local refSqlName=generateSQLName(propertyDescriptor.referencedDescriptor.name[propertyDescriptor.referencedDescriptor.name?last_index_of(".")+1..])/>
  </#if>
  <#local isReferenceEntity=propertyDescriptor.referencedDescriptor.entity/>
  <#local isPurelyAbstract=propertyDescriptor.referencedDescriptor.purelyAbstract/>
  <#local oneToOne=propertyDescriptor.oneToOne/>
  <#local composition=propertyDescriptor.composition/>
  <#if propertyDescriptor.reverseRelationEnd??>
    <#local bidirectional=true/>
    <#local reversePropertyName=propertyDescriptor.reverseRelationEnd.name/>
    <#if instanceof(propertyDescriptor.reverseRelationEnd, "org.jspresso.framework.model.descriptor.IReferencePropertyDescriptor")>
      <#local componentName=componentDescriptor.name[componentDescriptor.name?last_index_of(".")+1..]/>
      <#local elementName=propertyType[propertyType?last_index_of(".")+1..]/>
      <#--
      <#if (compareStrings(elementName, componentName) != 0)>
        <#local reverseOneToOne=(compareStrings(elementName, componentName) < 0)/>
      <#else>
        Reflexive one to one
        <#local reverseOneToOne=(compareStrings(propertyName, reversePropertyName) < 0)/>
      </#if>
      -->
      <#local reverseOneToOne = !propertyDescriptor.leadingPersistence/>
    <#else>
      <#local reverseOneToOne=false/>
      <#if propertyDescriptor.reverseRelationEnd.modelTypeName?starts_with("java.util.List")>
        <#local managesPersistence=false/>
      <#else>
        <#local managesPersistence=true/>
      </#if>
    </#if>
  <#else>
    <#local bidirectional=false/>
    <#local reverseOneToOne=false/>
  </#if>
  <#if propertyDescriptor.sqlName??>
    <#local propSqlName=propertyDescriptor.sqlName/>
    <#local propSqlNameGenerated = false/>
    <#local fieldName=propertyDescriptor.sqlName/>
  <#else>
    <#local propSqlName=generateSQLName(propertyName)/>
    <#local propSqlNameGenerated = true/>
  </#if>
  /**
   * Gets the ${propertyName}.
   *
  <#if !propertyDescriptor.computed>
    <#if isReferenceEntity>
      <#if reverseOneToOne>
   * @hibernate.one-to-one
        <#if composition>
   *           cascade = "persist,merge,save-update,delete"
        </#if>
   *           property-ref = "${propertyDescriptor.reverseRelationEnd.name}"
        <#if !propertyDescriptor.versionControl>
   *           optimistic-lock = "false"
        </#if>
        <#if propertyDescriptor.fetchType??>
          <#if propertyDescriptor.fetchType.toString() = "JOIN">
   *           fetch = "join"
          <#elseif propertyDescriptor.fetchType.toString() = "SUBSELECT">
   *           fetch = "subselect"
          </#if>
        </#if>
        <#if propertyDescriptor.batchSize??>
   *           batch-size = "${propertyDescriptor.batchSize?c}"
        </#if>
      <#else>
   * @hibernate.many-to-one
        <#if !propertyDescriptor.versionControl>
   *           optimistic-lock = "false"
        </#if>
        <#if oneToOne>
          <#if composition>
   *           cascade = "persist,merge,save-update,delete"
          </#if>
   *           unique = "true"
        <#elseif bidirectional>
          <#if !managesPersistence>
   *           insert = "false"
   *           update = "false"
   *           not-null = "true"
          </#if>
        <#else>
          <#if composition>
   *           cascade = "persist,merge,save-update,delete"
          <#else>
               <#-- Referential relationship -->
   *           cascade = "none"
          </#if>
        </#if>
        <#if propertyDescriptor.fetchType??>
          <#if propertyDescriptor.fetchType.toString() = "JOIN">
   *           fetch = "join"
          <#elseif propertyDescriptor.fetchType.toString() = "SUBSELECT">
   *           fetch = "subselect"
          </#if>
        </#if>
        <#if propertyDescriptor.batchSize??>
   *           batch-size = "${propertyDescriptor.batchSize?c}"
        </#if>
        <#if isEntity>
          <#if fkName??>
   *           foreign-key = "${reduceSQLName(dedupSQLName(fkName))}"
          <#else>
   *           foreign-key = "${reduceSQLName(dedupSQLName(tableName+"_"+propSqlName),"_FK")}"
          </#if>
        </#if>
   * @hibernate.column
        <#if propSqlNameGenerated>
   *           name = "${reduceSQLName(propSqlName,"_ID")}"
        <#else>
   *           name = "${reduceSQLName(propSqlName)}"
        </#if>
        <#if propertyDescriptor.mandatory>
   *           not-null = "true"
        </#if>
        <#if propertyDescriptor.unicityScope??>
   *           unique-key = "${reduceSQLName(generateSQLName(propertyDescriptor.unicityScope),"_UNQ")}"
        </#if>
      </#if>
    <#elseif !isPurelyAbstract>
   * @hibernate.component
   *           prefix = "${propSqlName}_"
      <#if !propertyDescriptor.versionControl>
   *           optimistic-lock = "false"
      </#if>
    <#else>
   * @hibernate.any
      <#if idTypeName??>
   *           id-type = "${idTypeName}"
      <#else>
   *           id-type = "string"
      </#if>
      <#if !propertyDescriptor.versionControl>
   *           optimistic-lock = "false"
      </#if>
   * @hibernate.any-column
   *           name = "${reduceSQLName(propSqlName,"_NAME")}"
      <#if propertyDescriptor.unicityScope??>
   *           unique-key = "${reduceSQLName(generateSQLName(propertyDescriptor.unicityScope),"_UNQ")}"
      </#if>
   * @hibernate.any-column
      <#if propSqlNameGenerated>
   *           name = "${reduceSQLName(propSqlName,"_ID")}"
      <#else>
   *           name = "${reduceSQLName(propSqlName)}"
      </#if>
      <#if idTypeLength??>
   *           length = "${idTypeLength?c}"
      </#if>
      <#if propertyDescriptor.mandatory>
   *           not-null = "true"
      </#if>
      <#if propertyDescriptor.unicityScope??>
   *           unique-key = "${reduceSQLName(generateSQLName(propertyDescriptor.unicityScope),"_UNQ")}"
      </#if>
    </#if>
  </#if>
   * @return the ${propertyName}.
   */
  <#if !propertyDescriptor.computed && fieldName??>
  @org.springframework.data.mongodb.core.mapping.Field("${fieldName}")
  </#if>
  ${propertyType?replace("$", ".")} get${propertyName?cap_first}();

</#macro>

<#macro generatePropertyNameConstant propertyDescriptor>
  <#local propertyName=propertyDescriptor.name/>
  /**
   * Constant value for ${propertyName}.
   */
  String ${generateSQLName(propertyName)} = "${propertyName}";

</#macro>

<#macro generateEnumerationConstants propertyDescriptor>
  <#local propertyName=propertyDescriptor.name/>
  <#list propertyDescriptor.enumerationValues as enumerationValue>
  /**
   * Constant enumeration value for ${propertyName} : ${enumerationValue}.
   */
  String ${generateSQLName(propertyName + "_" + enumerationValue)} = "${enumerationValue}";

  </#list>
</#macro>

<#macro generateCollectionPropertyAccessors componentDescriptor propertyDescriptor overridden>
  <#if !overridden>
    <@generatePropertyNameConstant propertyDescriptor=propertyDescriptor/>
  </#if>
  <@generateCollectionGetter componentDescriptor=componentDescriptor propertyDescriptor=propertyDescriptor/>
  <#if propertyDescriptor.modifiable>
    <@generateCollectionSetter componentDescriptor=componentDescriptor propertyDescriptor=propertyDescriptor/>
    <@generateCollectionAdder componentDescriptor=componentDescriptor propertyDescriptor=propertyDescriptor/>
    <@generateCollectionRemer componentDescriptor=componentDescriptor propertyDescriptor=propertyDescriptor/>
  </#if>

</#macro>

<#macro generateReferencePropertyAccessors componentDescriptor propertyDescriptor overridden>
  <#if !overridden>
    <@generatePropertyNameConstant propertyDescriptor=propertyDescriptor/>
  </#if>
  <@generateComponentRefGetter componentDescriptor=componentDescriptor propertyDescriptor=propertyDescriptor/>
  <#if propertyDescriptor.modifiable>
    <@generateEntityRefSetter componentDescriptor=componentDescriptor propertyDescriptor=propertyDescriptor/>
  </#if>

</#macro>

<#macro generateScalarPropertyAccessors componentDescriptor propertyDescriptor overridden>
  <#if !overridden>
    <@generatePropertyNameConstant propertyDescriptor=propertyDescriptor/>
    <#if instanceof(propertyDescriptor, "org.jspresso.framework.model.descriptor.IEnumerationPropertyDescriptor")>
      <@generateEnumerationConstants propertyDescriptor=propertyDescriptor/>
    </#if>
  </#if>
  <@generateScalarGetter componentDescriptor=componentDescriptor propertyDescriptor=propertyDescriptor overridden=overridden/>
  <#if propertyDescriptor.modifiable>
    <@generateScalarSetter componentDescriptor=componentDescriptor propertyDescriptor=propertyDescriptor/>
  </#if>

</#macro>

<#macro generatePropertyAccessors componentDescriptor propertyDescriptor>
  <#assign overridden=false/>
  <#if componentDescriptor.ancestorDescriptors??>
    <#list componentDescriptor.ancestorDescriptors as ancestorDescriptor>
      <#list ancestorDescriptor.propertyDescriptors as ancestorPropertyDescriptor>
        <#if ancestorPropertyDescriptor.name == propertyDescriptor.name>
          <#assign overridden=true/>
        </#if>
      </#list>
    </#list>
  </#if>
  <#if instanceof(propertyDescriptor, "org.jspresso.framework.model.descriptor.ICollectionPropertyDescriptor")>
    <@generateCollectionPropertyAccessors componentDescriptor=componentDescriptor propertyDescriptor=propertyDescriptor overridden=overridden/>
  <#elseif instanceof(propertyDescriptor, "org.jspresso.framework.model.descriptor.IReferencePropertyDescriptor")>
    <@generateReferencePropertyAccessors componentDescriptor=componentDescriptor propertyDescriptor=propertyDescriptor overridden=overridden/>
  <#else>
    <@generateScalarPropertyAccessors componentDescriptor=componentDescriptor propertyDescriptor=propertyDescriptor overridden=overridden/>
  </#if>

</#macro>

<#macro generateClassSource componentDescriptor translationInnerClass>
  <@generateClassHeader componentDescriptor=componentDescriptor translationInnerClass=translationInnerClass/>
  <#if componentDescriptor.declaredPropertyDescriptors??>
    <#assign empty=true/>
    <#list componentDescriptor.declaredPropertyDescriptors as propertyDescriptor>
      <#if propertyDescriptor.name != "id" && propertyDescriptor.name != "version" && (!propertyDescriptor.computed || instanceof(propertyDescriptor, "org.jspresso.framework.model.descriptor.IStringPropertyDescriptor") && (propertyDescriptor.translatable || propertyDescriptor.name?ends_with("Nls") || propertyDescriptor.name?ends_with("Raw")))>
        <@generatePropertyAccessors componentDescriptor=componentDescriptor propertyDescriptor=propertyDescriptor/>
        <#assign empty=false/>
      </#if>
    </#list>
    <#if empty>
    // THIS IS JUST A MARKER INTERFACE.
    </#if>
  <#else>
    // THIS IS JUST A MARKER INTERFACE.
  </#if>
</#macro>

<#macro generateExtensionSource componentDescriptor>
<#--
  <@generateClassHeader componentDescriptor=componentDescriptor translationInnerClass=false/>
-->
  <#local componentName=componentDescriptor.name[componentDescriptor.name?last_index_of(".")+1..]/>

/**
 * ${componentName} extension interface holding all computed properties.
 */
public interface I${componentName}Extension {

  <#if componentDescriptor.declaredPropertyDescriptors??>
    <#assign empty=true/>
    <#list componentDescriptor.declaredPropertyDescriptors as propertyDescriptor>
      <#if propertyDescriptor.computed && (!instanceof(propertyDescriptor, "org.jspresso.framework.model.descriptor.IStringPropertyDescriptor") || !(propertyDescriptor.translatable || propertyDescriptor.name?ends_with("Nls") || propertyDescriptor.name?ends_with("Raw")))>
        <@generatePropertyAccessors componentDescriptor=componentDescriptor propertyDescriptor=propertyDescriptor/>
        <#assign empty=false/>
      </#if>
    </#list>
    <#if empty>
  // THIS IS JUST A MARKER INTERFACE.
    </#if>
  <#else>
  // THIS IS JUST A MARKER INTERFACE.
  </#if>
</#macro>
<@generatePackageHeader componentDescriptor=componentDescriptor/>
<#if extension>
  <@generateExtensionSource componentDescriptor=componentDescriptor/>
<#else>
  <@generateClassSource componentDescriptor=componentDescriptor translationInnerClass=false/>
  <#if componentDescriptor.translatable && !componentDescriptor.purelyAbstract>
    <@generateClassSource componentDescriptor=componentTranslationsDescriptor.referencedDescriptor.elementDescriptor translationInnerClass=true/>
}
  </#if>
</#if>
}
