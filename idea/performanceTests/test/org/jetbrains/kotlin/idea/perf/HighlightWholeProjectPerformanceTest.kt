/*
 * Copyright 2010-2020 JetBrains s.r.o. and Kotlin Programming Language contributors.
 * Use of this source code is governed by the Apache 2.0 license that can be found in the license/LICENSE.txt file.
 */

package org.jetbrains.kotlin.idea.perf

import com.intellij.openapi.module.impl.ProjectLoadingErrorsHeadlessNotifier
import org.jetbrains.kotlin.idea.perf.Stats.Companion.printStatValue
import org.jetbrains.kotlin.idea.perf.Stats.Companion.tcSuite
import org.jetbrains.kotlin.idea.testFramework.ProjectOpenAction
import org.jetbrains.kotlin.idea.testFramework.logMessage
import java.io.File

class HighlightWholeProjectPerformanceTest : AbstractPerformanceProjectsTest() {

    companion object {

        @JvmStatic
        val hwStats: Stats = Stats("helloWorld project")

        @JvmStatic
        val warmUp = WarmUpProject(hwStats)

        init {
            // there is no @AfterClass for junit3.8
            Runtime.getRuntime().addShutdownHook(Thread { hwStats.close() })
        }

    }

    override fun setUp() {
        super.setUp()
        warmUp.warmUp(this)

        val allowedErrorDescription = setOf(
            "Unknown artifact type: war",
            "Unknown artifact type: exploded-war"
        )

        ProjectLoadingErrorsHeadlessNotifier.setErrorHandler(
            { errorDescription ->
                val description = errorDescription.description
                if (description !in allowedErrorDescription) {
                    throw RuntimeException(description)
                } else {
                    logMessage { "project loading error: '$description' at '${errorDescription.elementName}'" }
                }
            }, testRootDisposable
        )
    }

    fun testHighlightAllKtFilesInProject() {
        val projectSpecs = projectSpecs()
        for (projectSpec in projectSpecs) {
            val projectName = projectSpec.name
            val projectPath = projectSpec.path

            val suiteName = "$projectName project"
            try {
                tcSuite(suiteName) {
                    val stats = Stats(suiteName)
                    stats.use { stat ->
                        perfGradleBasedProject(projectName, projectPath, stat)
                        //perfJpsBasedProject(projectName, stat)

                        val project = myProject!!
                        project.projectFilePath
                        val projectDir = File(projectPath)
                        val ktFiles = projectDir.allFilesWithExtension("kt").toList()
                        printStatValue("$suiteName: number of kt files", ktFiles.size)
                        val sortedBySize = ktFiles
                            .filter { it.length() > 0 }
                            .map { it.path to it.length() }.sortedBy { it.second }
                        val tenPercentOfFiles = sortedBySize.size / 10

                        val top10Files = sortedBySize.take(tenPercentOfFiles).map { it.first }
                        val mid10Files =
                            sortedBySize.take(sortedBySize.size / 2 + tenPercentOfFiles / 2).takeLast(tenPercentOfFiles).map { it.first }
                        val last10Files = sortedBySize.takeLast(tenPercentOfFiles).map { it.first }

                        val topMidLastFiles = LinkedHashSet(top10Files + mid10Files + last10Files)
                        printStatValue("$suiteName: limited number of kt files", topMidLastFiles.size)

                        topMidLastFiles.forEach { path ->
                            val localPath = path.substring(path.indexOf(projectPath) + projectPath.length + 1)
                            try {
                                // 1x3 it not good enough for statistics, but at least it gives some overview
                                perfHighlightFile(
                                    project(),
                                    fileName = localPath,
                                    stats = stat,
                                    warmUpIterations = 1,
                                    iterations = 3,
                                    checkStability = false
                                )
                            } catch (e: Throwable) {
                                // nothing as it is already caught by perfTest
                            }
                        }
                    }
                }
            } catch (e: Throwable) {
                // don't fail entire test on a single failure
            }
        }
    }

    private fun perfGradleBasedProject(name: String, path: String, stats: Stats) {
        myProject = perfOpenProject(
            name = name,
            stats = stats,
            note = "",
            path = path,
            openAction = ProjectOpenAction.GRADLE_PROJECT,
            fast = true
        )
    }

    private fun perfJpsBasedProject(name: String, stats: Stats) {
        myProject = perfOpenProject(
            name = name,
            stats = stats,
            note = "",
            path = "../$name",
            openAction = ProjectOpenAction.EXISTING_IDEA_PROJECT,
            fast = true
        )
    }

    private fun projectSpecs(): List<ProjectSpec> {
        val projects = System.getProperty("performanceProjects") ?: return emptyList()
        return projects.split(",").map {
            val idx = it.indexOf("=")
            if (idx <= 0) ProjectSpec(it, "../$it") else ProjectSpec(it.substring(0, idx), it.substring(idx + 1))
        }.filter {
            val path = File(it.path)
            path.exists() && path.isDirectory
        }
    }

    private data class ProjectSpec(val name: String, val path: String)
}