package org.jspresso.maven;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import java.io.File;
import java.net.URL;
import java.util.Date;
import java.util.List;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.jspresso.framework.tools.entitygenerator.EntityGenerator;
import org.slf4j.impl.StaticLoggerBinder;

/**
 * Goal which generates entities for a Jspresso project.
 */
@Mojo(name = "generate-entities", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE)
public class EntityGeneratorMojo extends AbstractMojo {

  /**
   * The Maven project.
   */
  @Parameter(defaultValue = "${project}", required = true, readonly = true)
  MavenProject                                             project;

  /**
   * The source directory containing dsl files needed for change detection.
   */
  @Parameter(required = false)
  private File[]                                           sourceDirs;

  /**
   * Uses given selector too lookup the bean ref factory context file. If not
   * set, defaults to beanRefFactory.xml.
   */
  @Parameter(required = true)
  private String                                           beanFactorySelector;

  /**
   * Uses given applicationContextKey as registered in the spring
   * BeanFactoryLocator.
   */
  @Parameter(required = true)
  private String                                           applicationContextKey;

  /**
   * Generates code for the given component descriptor identifiers in the
   * application context.
   */
  @Parameter(required = false)
  private String[]                                         componentIds;

  /**
   * Excludes classes whose names match the regular expression.
   */
  @Parameter(required = false)
  private String[]                                         excludePatterns;

  /**
   * Generates java5 annotations (incompatible with XDoclet as of now).
   */
  @Parameter(defaultValue = "false", required = false)
  private boolean                                          generateAnnotations;

  /**
   * Generates code for the component descriptors declared in the listed
   * packages.
   */
  @Parameter(property = "includePackages", required = false)
  private String[]                                         includePackages;

  /**
   * Configures a maximum size for the generated SQL mapping names.
   */
  @Parameter(defaultValue="-1", property = "maxSqlNameSize", required = false)
  private int                                              maxSqlNameSize;

  /**
   * Sets the output directory for generated source.
   */
  @Parameter(defaultValue = "${project.build.directory}/generated-sources/entitygenerator", required = true)
  private File                                             outputDir;

  /**
   * Sets the file extension for generated source.
   */
  @Parameter(defaultValue = "java", required = true)
  private String                                           fileExtension;

  /**
   * Prepends a prefix to generated class names.
   */
  @Parameter(required = false)
  private String                                           classnamePrefix;

  /**
   * Appends a suffix to generated class names.
   */
  @Parameter(required = false)
  private String                                           classnameSuffix;

  /**
   * Sets the used component code template.
   */
  @Parameter(defaultValue = "HibernateXdocletEntity.ftl", required = true)
  private String                                           templateName;

  /**
   * Sets the path to lookup the template from.
   */
  @Parameter(defaultValue = "/org/jspresso/framework/tools/entitygenerator", required = true)
  private String                                           templateResourcePath;

  @Parameter(defaultValue = "${plugin.classRealm}", required = true, readonly = true)
  private org.codehaus.plexus.classworlds.realm.ClassRealm classRealm;

  /**
   * Triggers thee execution of EntityGenerator.
   * <p>
   * {@inheritDoc}
   */
  @Override
  public void execute() throws MojoExecutionException {
    if (sourceDirs == null) {
      sourceDirs = new File[] {
          new File(project.getBasedir(), "src/main/resources"),
          new File(project.getBasedir(), "target/generated-resources/dsl"),
      };
    }
    // bind slf4j to maven log
    StaticLoggerBinder.getSingleton().setLog(getLog());
    if (isChangeDetected()) {
      setupPluginClasspath();
      runEntityGenerator();
    } else {
      getLog().info("No change detected. Skipping generation.");
    }
    project.addCompileSourceRoot(outputDir.getPath());
  }

  private void runEntityGenerator() {
    EntityGenerator generator = new EntityGenerator();
    generator.setBeanFactorySelector(beanFactorySelector);
    generator.setApplicationContextKey(applicationContextKey);
    generator.setTemplateResourcePath(templateResourcePath);
    generator.setTemplateName(templateName);
    generator.setOutputDir(outputDir.getAbsolutePath());
    generator.setFileExtension(fileExtension);
    generator.setClassnamePrefix(classnamePrefix);
    generator.setClassnameSuffix(classnameSuffix);
    generator.setIncludePackages(includePackages);
    generator.setExcludePatterns(excludePatterns);
    generator.setGenerateAnnotations(generateAnnotations);
    generator.setComponentIds(componentIds);
    generator.setMaxSqlNameSize(maxSqlNameSize);
    generator.generateComponents();
  }

  private boolean isChangeDetected() {
    if (!outputDir.exists() || outputDir.list().length == 0) {
      return true;
    }
    long outputLastModified = latestModified(outputDir,
        outputDir.lastModified());
    for (File sourceDir : sourceDirs) {
      getLog().info("Scanning for changes : " + sourceDir.getAbsolutePath());
      if (hasChangedModelDslFile(sourceDir, outputLastModified)) {
        return true;
      }
    }
    return false;
  }

  private long latestModified(File root, long maxLastModified) {
    long latest = maxLastModified;
    if (root.lastModified() > maxLastModified) {
      latest = root.lastModified();
    }
    if (root.isDirectory()) {
      for (File child : root.listFiles()) {
        latest = latestModified(child, latest);
      }
    }
    return latest;
  }

  private boolean hasChangedModelDslFile(File source, long maxLastModified) {
    if (source.isDirectory()) {
      for (File childSource : source.listFiles()) {
        if (hasChangedModelDslFile(childSource, maxLastModified)) {
          return true;
        }
      }
    } else if (source.getName().toLowerCase().indexOf("model") >= 0) {
      if (source.lastModified() > maxLastModified) {
        getLog().info(
            "Detected a change on resource " + source.toString() + ". "
                + new Date(source.lastModified()) + " > "
                + new Date(maxLastModified));
        return true;
      }
    }
    return false;
  }

  private void setupPluginClasspath() throws MojoExecutionException {
    try {
      for (File sourceDir : sourceDirs) {
        classRealm.addURL(sourceDir.toURI().toURL());
        getLog().debug(
            "Adding element to plugin classpath " + sourceDir.getPath());
      }
      List<String> compileClasspathElements = project
          .getCompileClasspathElements();
      for (String element : compileClasspathElements) {
        if (!element.equals(project.getBuild().getOutputDirectory())
            && !(element.indexOf("log4j") >= 0)) {
          File elementFile = new File(element);
          getLog().debug(
              "Adding element to plugin classpath " + elementFile.getPath());
          URL url = elementFile.toURI().toURL();
          classRealm.addURL(url);
        }
      }
    } catch (Exception ex) {
      throw new MojoExecutionException(ex.toString(), ex);
    }
  }

}