/*
 * Copyright (C) 2017 Black Duck Software Inc.
 * http://www.blackducksoftware.com/
 * All rights reserved.
 *
 * This software is the confidential and proprietary information of
 * Black Duck Software ("Confidential Information"). You shall not
 * disclose such Confidential Information and shall use it only in
 * accordance with the terms of the license agreement you entered into
 * with Black Duck Software.
 */
package com.blackducksoftware.integration.hub.packman.bomtool.pip

import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

import com.blackducksoftware.integration.hub.bdio.simple.model.DependencyNode
import com.blackducksoftware.integration.hub.bdio.simple.model.Forge
import com.blackducksoftware.integration.hub.bdio.simple.model.externalid.NameVersionExternalId
import com.blackducksoftware.integration.hub.packman.PackmanProperties
import com.blackducksoftware.integration.hub.packman.type.BomToolType
import com.blackducksoftware.integration.hub.packman.util.FileFinder
import com.blackducksoftware.integration.hub.packman.util.ProjectInfoGatherer
import com.blackducksoftware.integration.hub.packman.util.executable.Executable
import com.blackducksoftware.integration.hub.packman.util.executable.ExecutableRunner
import com.blackducksoftware.integration.hub.packman.util.executable.ExecutableRunnerException

@Component
class PipPackager {
    final Logger logger = LoggerFactory.getLogger(this.getClass())

    @Autowired
    FileFinder fileFinder

    @Autowired
    ExecutableRunner executableRunner

    @Autowired
    PackmanProperties packmanProperties

    @Autowired
    ProjectInfoGatherer projectInfoGatherer

    List<DependencyNode> makeDependencyNodes(final String sourcePath, final String pipExecutable, final String pythonExecutable) throws ExecutableRunnerException {
        def sourceDirectory = new File(sourcePath)
        def outputDirectory = new File(packmanProperties.outputDirectoryPath)
        def setupFile = fileFinder.findFile(sourceDirectory, 'setup.py')

        def pipInspectorOptions = [
            getClass().getResource('pip-inpspector.py').toString()
        ]

        // Install requirements file and add it as an option for the inspector
        if (packmanProperties.requirementsFilePath) {
            def requirementsFile = new File(packmanProperties.requirementsFilePath)
            pipInspectorOptions += [
                '-r',
                requirementsFile.absolutePath
            ]

            def installRequirements = new Executable(sourceDirectory, pipExecutable, [
                'install',
                '-r',
                requirementsFile.absolutePath
            ])
            executableRunner.executeLoudly(installRequirements)
        }

        // Install project if it can find one and pass its name to the inspector
        if(setupFile) {
            def installProjectExecutable = new Executable(sourceDirectory, pipExecutable, ['install', '.', '-I'])
            executableRunner.executeLoudly(installProjectExecutable)

            if(!packmanProperties.projectName) {
                def findProjectNameExecutable = new Executable(sourceDirectory, pythonExecutable, [
                    setupFile.absolutePath,
                    '--name'
                ])
                def projectName = executableRunner.executeQuietly(findProjectNameExecutable).standardOutput.trim()
                pipInspectorOptions += ['-p', projectName]
            }
        }

        def pipInspector = new Executable(sourceDirectory, pythonExecutable, pipInspectorOptions)
        def inspectorOutput = executableRunner.executeLoudly(pipInspector).standardOutput
        def parser = new PipInspectorTreeParser()
        DependencyNode project = parser.parse(inspectorOutput)

        if(project.name == PipInspectorTreeParser.UNKOWN_PROJECT) {
            project.name = projectInfoGatherer.getDefaultProjectName(BomToolType.PIP, sourcePath)
            project.version = projectInfoGatherer.getDefaultProjectVersionName()
            project.externalId = new NameVersionExternalId(Forge.PYPI, project.name, project.version)
        }

        [project]
    }
}
