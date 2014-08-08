package org.jicunit.maven

import org.apache.maven.plugin.AbstractMojo

/**
 * Provides support for Maven plugins implemented in Groovy.
 *
 */
abstract class GroovyMojoSupport
extends AbstractMojo {
  protected AntBuilder ant = new AntBuilder()
}