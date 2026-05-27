/*
 * Copyright (c) 2026 Anatoly S. Krivitsky, Ph.D.
 * All rights reserved.
 *
 * Permission is granted, free of charge, to use, copy, distribute, and publish
 * this code, provided that this copyright notice is preserved.
 *
 * This code is provided "as is", without warranty of any kind. The user assumes
 * all responsibility and risk for its use. Under no circumstances shall the
 * author be liable for any direct, indirect, incidental, special, consequential,
 * exemplary, or punitive damages, lost profits, data loss, or other losses
 * arising from the use of, inability to use, or reliance on this code.
 *
 * Questions and comments:
 *   akrivitsky@yahoo.com
 *   akrivitsky@gmail.com
 */
package dev.anatoly.swaggerdemo.cucumber;

import org.junit.platform.suite.api.ConfigurationParameter;
import org.junit.platform.suite.api.IncludeEngines;
import org.junit.platform.suite.api.SelectClasspathResource;
import org.junit.platform.suite.api.Suite;

import static io.cucumber.junit.platform.engine.Constants.GLUE_PROPERTY_NAME;

@Suite
@IncludeEngines("cucumber")
@SelectClasspathResource("features/books")
@ConfigurationParameter(key = GLUE_PROPERTY_NAME, value = "dev.anatoly.swaggerdemo.cucumber")
public class RunCucumberTest {
}