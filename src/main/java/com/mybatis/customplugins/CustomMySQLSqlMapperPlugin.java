package com.mybatis.customplugins;

import static org.mybatis.generator.internal.util.StringUtility.stringHasValue;
import static org.mybatis.generator.internal.util.messages.Messages.getString;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.TreeSet;

import org.mybatis.generator.api.CommentGenerator;
import org.mybatis.generator.api.GeneratedJavaFile;
import org.mybatis.generator.api.GeneratedXmlFile;
import org.mybatis.generator.api.IntrospectedColumn;
import org.mybatis.generator.api.IntrospectedTable;
import org.mybatis.generator.api.PluginAdapter;
import org.mybatis.generator.api.dom.OutputUtilities;
import org.mybatis.generator.api.dom.java.CompilationUnit;
import org.mybatis.generator.api.dom.java.Field;
import org.mybatis.generator.api.dom.java.FullyQualifiedJavaType;
import org.mybatis.generator.api.dom.java.Interface;
import org.mybatis.generator.api.dom.java.JavaVisibility;
import org.mybatis.generator.api.dom.java.Method;
import org.mybatis.generator.api.dom.java.Parameter;
import org.mybatis.generator.api.dom.java.PrimitiveTypeWrapper;
import org.mybatis.generator.api.dom.java.TopLevelClass;
import org.mybatis.generator.api.dom.xml.Attribute;
import org.mybatis.generator.api.dom.xml.Document;
import org.mybatis.generator.api.dom.xml.TextElement;
import org.mybatis.generator.api.dom.xml.XmlElement;
import org.mybatis.generator.codegen.XmlConstants;
import org.mybatis.generator.codegen.mybatis3.ListUtilities;
import org.mybatis.generator.codegen.mybatis3.MyBatis3FormattingUtilities;
import org.mybatis.generator.config.GeneratedKey;
import org.mybatis.generator.config.PropertyRegistry;
import org.mybatis.generator.exception.ShellException;

public class CustomMySQLSqlMapperPlugin extends PluginAdapter {
	// private IntrospectedTable introspectedTable;

	private static final String EXTENSION_POSTFIX = "Ext";

	@Override
	public void initialized(IntrospectedTable introspectedTable) {
		super.initialized(introspectedTable);
	}

	@Override
	public boolean clientGenerated(Interface interfaze, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		/*
		 * 增加批量新增的方法
		 */
		addInsertByBatchMethod(interfaze, topLevelClass, introspectedTable);
		// TODO
		// addinsertSelectiveByBatchMethod(interfaze, topLevelClass,
		// introspectedTable);

		return super.clientGenerated(interfaze, topLevelClass, introspectedTable);
	}

	/**
	 * 增加批量新增的方法（参考已有的逻辑InsertMethodGenerator）
	 * 
	 * @param interfaze
	 * @param topLevelClass
	 * @param introspectedTable
	 */
	private void addInsertByBatchMethod(Interface interfaze, TopLevelClass topLevelClass,
			IntrospectedTable introspectedTable) {
		Set<FullyQualifiedJavaType> importedTypes = new TreeSet<FullyQualifiedJavaType>();
		Method method = new Method();

		method.setReturnType(FullyQualifiedJavaType.getIntInstance());
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("insertByBatch");

		FullyQualifiedJavaType parameterType = new FullyQualifiedJavaType(
				"java.util.List<" + introspectedTable.getBaseRecordType() + ">");
		// if (isSimple) {
		// parameterType = new
		// FullyQualifiedJavaType(introspectedTable.getBaseRecordType());
		// } else {
		// parameterType =
		// introspectedTable.getRules().calculateAllFieldsClass();
		// }

		importedTypes.add(parameterType);

		method.addParameter(new Parameter(parameterType, "recordList", "@Param(\"recordList\")")); //$NON-NLS-1$

		context.getCommentGenerator().addGeneralMethodComment(method, introspectedTable);

		interfaze.addMethod(method);

		// addMapperAnnotations(interfaze, method);

		// if (context.getPlugins().clientGenerated(method, interfaze,
		// introspectedTable)) {
		// interfaze.addImportedTypes(importedTypes);
		// interfaze.addMethod(method);
		// }
	}

	@Override
	public boolean sqlMapDocumentGenerated(Document document, IntrospectedTable introspectedTable) {
		sqlMapInsertByBatchMethod(document, introspectedTable);
		return super.sqlMapDocumentGenerated(document, introspectedTable);
	}

	/**
	 * sqlXML文件中增加批量新增的脚步
	 * 
	 * @param document
	 * @param introspectedTable
	 */
	private void sqlMapInsertByBatchMethod(Document document, IntrospectedTable introspectedTable) {

		XmlElement answer = new XmlElement("insert"); //$NON-NLS-1$

		answer.addAttribute(new Attribute("id", "insertByBatch")); //$NON-NLS-1$

		// FullyQualifiedJavaType parameterType =
		// introspectedTable.getRules().calculateAllFieldsClass();

		answer.addAttribute(new Attribute("parameterType", "java.util.List")); //$NON-NLS-1$ //$NON-NLS-2$

		context.getCommentGenerator().addComment(answer);

		// TODO 批量是否要自动生成 id
		// GeneratedKey gk = introspectedTable.getGeneratedKey();
		// if (gk != null) {
		// IntrospectedColumn introspectedColumn =
		// introspectedTable.getColumn(gk.getColumn());
		// // if the column is null, then it's a configuration error. The
		// // warning has already been reported
		// if (introspectedColumn != null) {
		// if (gk.isJdbcStandard()) {
		// answer.addAttribute(new Attribute("useGeneratedKeys", "true"));
		// //$NON-NLS-1$ //$NON-NLS-2$
		// answer.addAttribute(new Attribute("keyProperty",
		// introspectedColumn.getJavaProperty())); //$NON-NLS-1$
		// answer.addAttribute(new Attribute("keyColumn",
		// introspectedColumn.getActualColumnName())); //$NON-NLS-1$
		// } else {
		// answer.addElement(getSelectKey(introspectedColumn, gk));
		// }
		// }
		// }

		StringBuilder insertClause = new StringBuilder();
		StringBuilder valuesClause = new StringBuilder();

		insertClause.append("insert into "); //$NON-NLS-1$
		insertClause.append(introspectedTable.getFullyQualifiedTableNameAtRuntime());
		insertClause.append(" ("); //$NON-NLS-1$

		valuesClause.append("(");

		String recordList = "recordList";
		String record = "record";

		List<String> valuesClauses = new ArrayList<String>();
		List<IntrospectedColumn> columns = ListUtilities
				.removeIdentityAndGeneratedAlwaysColumns(introspectedTable.getAllColumns());
		for (int i = 0; i < columns.size(); i++) {
			IntrospectedColumn introspectedColumn = columns.get(i);

			insertClause.append(MyBatis3FormattingUtilities.getEscapedColumnName(introspectedColumn));
			valuesClause.append(MyBatis3FormattingUtilities.getParameterClause(introspectedColumn, record + "."));
			if (i + 1 < columns.size()) {
				insertClause.append(", "); //$NON-NLS-1$
				valuesClause.append(", "); //$NON-NLS-1$
			}

			if (valuesClause.length() > 80) {
				answer.addElement(new TextElement(insertClause.toString()));
				insertClause.setLength(0);
				OutputUtilities.xmlIndent(insertClause, 1);

				valuesClauses.add(valuesClause.toString());
				valuesClause.setLength(0);
				OutputUtilities.xmlIndent(valuesClause, 1);
			}
		}

		insertClause.append(") values ");
		answer.addElement(new TextElement(insertClause.toString()));

		valuesClause.append(")");

		valuesClauses.add(valuesClause.toString());

		XmlElement foreachElement = new XmlElement("foreach"); //$NON-NLS-1$
		foreachElement.addAttribute(new Attribute("collection", recordList)); //$NON-NLS-1$ //$NON-NLS-2$
		foreachElement.addAttribute(new Attribute("index", "index")); //$NON-NLS-1$ //$NON-NLS-2$
		foreachElement.addAttribute(new Attribute("item", record)); //$NON-NLS-1$ //$NON-NLS-2$
		foreachElement.addAttribute(new Attribute("separator", ",")); //$NON-NLS-1$ //$NON-NLS-2$

		for (String clause : valuesClauses) {
			foreachElement.addElement(new TextElement(clause));
		}

		answer.addElement(foreachElement);

		document.getRootElement().addElement(answer);
		// if
		// (context.getPlugins().sqlMapInsertSelectiveElementGenerated(answer,
		// introspectedTable)) {
		// parentElement.addElement(answer);
		// }

	}

	@Override
	public boolean modelExampleClassGenerated(TopLevelClass topLevelClass, IntrospectedTable introspectedTable) {
		// add field, getter, setter for limit clause
		// 是否要根据 mysql/oracle/db2 等不同类型的数据库做判断？ TODO
		addLimit(topLevelClass, introspectedTable, "pageStart");
		addLimit(topLevelClass, introspectedTable, "pageSize");
		return super.modelExampleClassGenerated(topLevelClass, introspectedTable);
	}

	@Override
	public boolean sqlMapSelectByExampleWithoutBLOBsElementGenerated(XmlElement element,
			IntrospectedTable introspectedTable) {
		// XmlElement isParameterPresenteElemen = (XmlElement) element
		// .getElements().get(element.getElements().size() - 1);
		XmlElement isNotNullElement = new XmlElement("if"); //$NON-NLS-1$
		isNotNullElement.addAttribute(new Attribute("test", "pageStart != null and pageStart>=0")); //$NON-NLS-1$ //$NON-NLS-2$
		// isNotNullElement.addAttribute(new Attribute("compareValue", "0"));
		// //$NON-NLS-1$ //$NON-NLS-2$
		isNotNullElement.addElement(new TextElement("limit #{pageStart} , #{pageSize}"));
		// isParameterPresenteElemen.addElement(isNotNullElement);
		element.addElement(isNotNullElement);
		return super.sqlMapUpdateByExampleWithoutBLOBsElementGenerated(element, introspectedTable);
	}

	private void addLimit(TopLevelClass topLevelClass, IntrospectedTable introspectedTable, String name) {
		CommentGenerator commentGenerator = context.getCommentGenerator();
		Field field = new Field();
		field.setVisibility(JavaVisibility.PROTECTED);
		// field.setType(FullyQualifiedJavaType.getIntInstance());
		field.setType(PrimitiveTypeWrapper.getIntegerInstance());
		field.setName(name);
		// field.setInitializationString("-1");
		commentGenerator.addFieldComment(field, introspectedTable);
		topLevelClass.addField(field);
		char c = name.charAt(0);
		String camel = Character.toUpperCase(c) + name.substring(1);
		Method method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setName("set" + camel);
		method.addParameter(new Parameter(PrimitiveTypeWrapper.getIntegerInstance(), name));
		method.addBodyLine("this." + name + "=" + name + ";");
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);
		method = new Method();
		method.setVisibility(JavaVisibility.PUBLIC);
		method.setReturnType(PrimitiveTypeWrapper.getIntegerInstance());
		method.setName("get" + camel);
		method.addBodyLine("return " + name + ";");
		commentGenerator.addGeneralMethodComment(method, introspectedTable);
		topLevelClass.addMethod(method);
	}

	@Override
	public boolean validate(List<String> warnings) {
		// TODO Auto-generated method stub
		return true;
	}

	/**
	 * 生成的 删除方法，只进行逻辑删除，即将对应的表中的 is_deleted 标记为 'y'
	 */
	@Override
	public boolean sqlMapDeleteByExampleElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
		System.out.println("enter CustomMySQLSqlMapperPlugin.sqlMapDeleteByExampleElementGenerated() ...");
		// XmlElement element = new XmlElement(element);
		element.setName("delete");
		element.getAttributes().clear();
		element.getElements().clear();
		String fqjt = introspectedTable.getExampleType();

		element.addAttribute(new Attribute("id", introspectedTable.getDeleteByExampleStatementId()));
		element.addAttribute(new Attribute("parameterType", fqjt));

		context.getCommentGenerator().addComment(element);

		StringBuilder sb = new StringBuilder();
		sb.append("update ");
		sb.append(introspectedTable.getAliasedFullyQualifiedTableNameAtRuntime());
		sb.append(" set is_deleted = 'y' ");
		element.addElement(new TextElement(sb.toString()));
		element.addElement(getExampleIncludeElement(introspectedTable));
		System.out.println("exit sqlMapDeleteByExampleElementGenerated()");
		return true;
	}

	protected XmlElement getExampleIncludeElement(IntrospectedTable introspectedTable) {
		XmlElement ifElement = new XmlElement("if");
		ifElement.addAttribute(new Attribute("test", "_parameter != null"));

		XmlElement includeElement = new XmlElement("include");
		includeElement.addAttribute(new Attribute("refid", introspectedTable.getExampleWhereClauseId()));
		ifElement.addElement(includeElement);

		return ifElement;
	}

	// @Override
	// public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles() {
	// String isOverwrite = (String) properties.get("isOverwrite");
	// isOverwrite = (String) context.getProperties().get("isOverwrite");
	// context.getJavaClientGeneratorConfiguration().getProperties();
	// System.out.println("isOverwrite : " + isOverwrite);
	// return null;
	// }

	/**
	 * 生成sql的扩展文件 mapperExt.xml 以及 mapperExt.java ，用户自定义的sql，都保存在扩展文件中
	 * 每次生成，会判断是否有存在的，有则不再重复生成
	 */
	@Override
	public List<GeneratedXmlFile> contextGenerateAdditionalXmlFiles(IntrospectedTable introspectedTable) {
		List<GeneratedXmlFile> xmlFileList = new ArrayList<>();

		String targetProject = context.getSqlMapGeneratorConfiguration().getTargetProject();
		String targetPackage = introspectedTable.getMyBatis3XmlMapperPackage();
		String fileName = introspectedTable.getMyBatis3XmlMapperFileName();
		fileName = fileName.replaceAll("Mapper", "Mapper" + EXTENSION_POSTFIX);
		// 判断ext文件是否已经存在，存在则不覆盖
		try {
			boolean isExtFileExist = checkExtFileExist(targetPackage, targetProject, fileName);
			// if (isExtFileExist) {
			// System.out.println(" the file : " + fileName +", is already exist
			// !");
			// return null;
			// }
		} catch (ShellException e) {
			// 需要异常处理 TODO
			return null;
		}

		// 生成 XXXMapperExt.xml 文件
		Document document = getXmlExtDocument(introspectedTable);
		GeneratedXmlFile xmlFile = new GeneratedXmlFile(document, fileName, targetPackage, targetProject, true,
				context.getXmlFormatter());
		xmlFileList.add(xmlFile);

		return xmlFileList;
	}

	/**
	 * 判断要生成的文件是否已经存在
	 * 
	 * @param targetPackage
	 * @param targetProject
	 * @param fileName
	 * @return
	 * @throws ShellException
	 */
	private boolean checkExtFileExist(String targetPackage, String targetProject, String fileName)
			throws ShellException {
		File project = new File(targetProject);
		if (!project.isDirectory()) {
			throw new ShellException(getString("Warning.9", //$NON-NLS-1$
					targetProject));
		}

		StringBuilder sb = new StringBuilder();
		StringTokenizer st = new StringTokenizer(targetPackage, "."); //$NON-NLS-1$
		while (st.hasMoreTokens()) {
			sb.append(st.nextToken());
			sb.append(File.separatorChar);
		}

		File directory = new File(project, sb.toString());
		if (!directory.isDirectory()) {
			boolean rc = directory.mkdirs();
			if (!rc) {
				throw new ShellException(getString("Warning.10", //$NON-NLS-1$
						directory.getAbsolutePath()));
			}
		}

		File targetFile = new File(directory, fileName);
		if (targetFile.exists()) {
			return true;
		}
		return false;
	}

	@Override
	public List<GeneratedJavaFile> contextGenerateAdditionalJavaFiles(IntrospectedTable introspectedTable) {

		List<GeneratedJavaFile> answer = new ArrayList<GeneratedJavaFile>();

		List<CompilationUnit> compilationUnits = generatorJavaFile(introspectedTable);
		if (compilationUnits != null && compilationUnits.size() > 0) {
			for (CompilationUnit compilationUnit : compilationUnits) {
				GeneratedJavaFile gjf = new GeneratedJavaFile(compilationUnit,
						context.getJavaClientGeneratorConfiguration().getTargetProject(),
						context.getProperty(PropertyRegistry.CONTEXT_JAVA_FILE_ENCODING), context.getJavaFormatter());
				answer.add(gjf);
			}
		}
		return answer;
	}

	private List<CompilationUnit> generatorJavaFile(IntrospectedTable introspectedTable) {
		// 生成 XXXMapperExt.java 文件
		CommentGenerator commentGenerator = context.getCommentGenerator();

		FullyQualifiedJavaType type = new FullyQualifiedJavaType(
				introspectedTable.getMyBatis3JavaMapperType() + EXTENSION_POSTFIX);
		// 判断是否存在已经生成的ext java类
		try {
			Class.forName(type.getFullyQualifiedName());
			// 存在，则不生成
			return null;
		} catch (ClassNotFoundException e) {
			// 如果不存在，则执行生成
		}

		Interface interfaze = new Interface(type);
		interfaze.setVisibility(JavaVisibility.PUBLIC);
		commentGenerator.addJavaFileComment(interfaze);

		String rootInterface = introspectedTable.getMyBatis3JavaMapperType();
		// if (!stringHasValue(rootInterface)) {
		// rootInterface = context.getJavaClientGeneratorConfiguration()
		// .getProperty(PropertyRegistry.ANY_ROOT_INTERFACE);
		// }

		if (stringHasValue(rootInterface)) {
			FullyQualifiedJavaType fqjt = new FullyQualifiedJavaType(rootInterface);
			interfaze.addSuperInterface(fqjt);
			interfaze.addImportedType(fqjt);
		}

		List<CompilationUnit> answer = new ArrayList<CompilationUnit>();
		if (context.getPlugins().clientGenerated(interfaze, null, introspectedTable)) {
			answer.add(interfaze);
		}
		return answer;
	}

	private Document getXmlExtDocument(IntrospectedTable introspectedTable) {

		Document document = new Document(XmlConstants.MYBATIS3_MAPPER_PUBLIC_ID,
				XmlConstants.MYBATIS3_MAPPER_SYSTEM_ID);
		document.setRootElement(getSqlMapElement(introspectedTable));

		return document;
	}

	private XmlElement getSqlMapElement(IntrospectedTable introspectedTable) {

		// FullyQualifiedTable table =
		// introspectedTable.getFullyQualifiedTable();
		// progressCallback.startTask(getString("Progress.12",
		// table.toString())); //$NON-NLS-1$
		XmlElement answer = new XmlElement("mapper"); //$NON-NLS-1$
		String namespace = introspectedTable.getMyBatis3SqlMapNamespace();
		answer.addAttribute(new Attribute("namespace", //$NON-NLS-1$
				namespace + EXTENSION_POSTFIX));

		context.getCommentGenerator().addRootComment(answer);

		return answer;
	}

	@Override
	public boolean sqlMapDeleteByPrimaryKeyElementGenerated(XmlElement element, IntrospectedTable introspectedTable) {
		// TODO Auto-generated method stub
		return super.sqlMapDeleteByPrimaryKeyElementGenerated(element, introspectedTable);
	}

}
