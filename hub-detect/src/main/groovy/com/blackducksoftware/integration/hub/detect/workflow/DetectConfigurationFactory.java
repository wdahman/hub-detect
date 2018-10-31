package com.blackducksoftware.integration.hub.detect.workflow;

import java.io.File;
import java.util.Arrays;
import java.util.List;

import com.blackducksoftware.integration.hub.detect.OverridableExcludedIncludedFilter;
import com.blackducksoftware.integration.hub.detect.configuration.DetectConfiguration;
import com.blackducksoftware.integration.hub.detect.configuration.DetectProperty;
import com.blackducksoftware.integration.hub.detect.configuration.PropertyAuthority;
import com.blackducksoftware.integration.hub.detect.tool.signaturescanner.BlackDuckSignatureScannerOptions;
import com.blackducksoftware.integration.hub.detect.workflow.bdio.BdioOptions;
import com.blackducksoftware.integration.hub.detect.workflow.file.AirGapOptions;
import com.blackducksoftware.integration.hub.detect.workflow.file.DirectoryOptions;
import com.blackducksoftware.integration.hub.detect.workflow.hub.DetectProjectServiceOptions;
import com.blackducksoftware.integration.hub.detect.workflow.project.ProjectNameVersionOptions;
import com.blackducksoftware.integration.hub.detect.workflow.search.SearchOptions;

public class DetectConfigurationFactory {
    DetectConfiguration detectConfiguration;

    public DetectConfigurationFactory(DetectConfiguration detectConfiguration) {
        this.detectConfiguration = detectConfiguration;
    }

    public DirectoryOptions createDirectoryOptions() {
        String sourcePath = detectConfiguration.getProperty(DetectProperty.DETECT_SOURCE_PATH, PropertyAuthority.DirectoryManager);
        String outputPath = detectConfiguration.getProperty(DetectProperty.DETECT_OUTPUT_PATH, PropertyAuthority.DirectoryManager);
        String bdioPath = detectConfiguration.getProperty(DetectProperty.DETECT_BDIO_OUTPUT_PATH, PropertyAuthority.DirectoryManager);
        String scanPath = detectConfiguration.getProperty(DetectProperty.DETECT_SCAN_OUTPUT_PATH, PropertyAuthority.DirectoryManager);

        return new DirectoryOptions(sourcePath, outputPath, bdioPath, scanPath);
    }

    public AirGapOptions createAirGapOptions() {
        String gradleOverride = detectConfiguration.getProperty(DetectProperty.DETECT_GRADLE_INSPECTOR_AIR_GAP_PATH, PropertyAuthority.AirGapManager);
        String nugetOverride = detectConfiguration.getProperty(DetectProperty.DETECT_NUGET_INSPECTOR_AIR_GAP_PATH, PropertyAuthority.AirGapManager);
        String dockerOverride = detectConfiguration.getProperty(DetectProperty.DETECT_DOCKER_INSPECTOR_AIR_GAP_PATH, PropertyAuthority.AirGapManager);

        return new AirGapOptions(dockerOverride, gradleOverride, nugetOverride);
    }

    public SearchOptions createSearchOptions(File directory) {
        List<String> excludedDirectories = Arrays.asList(detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_BOM_TOOL_SEARCH_EXCLUSION, PropertyAuthority.None));
        boolean forceNestedSearch = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BOM_TOOL_SEARCH_CONTINUE, PropertyAuthority.None);
        int maxDepth = detectConfiguration.getIntegerProperty(DetectProperty.DETECT_BOM_TOOL_SEARCH_DEPTH, PropertyAuthority.None);
        String excluded = detectConfiguration.getProperty(DetectProperty.DETECT_EXCLUDED_BOM_TOOL_TYPES, PropertyAuthority.None).toUpperCase();
        String included = detectConfiguration.getProperty(DetectProperty.DETECT_INCLUDED_BOM_TOOL_TYPES, PropertyAuthority.None).toUpperCase();
        OverridableExcludedIncludedFilter bomToolFilter = new OverridableExcludedIncludedFilter(excluded, included);
        return new SearchOptions(directory, excludedDirectories, forceNestedSearch, maxDepth, bomToolFilter);
    }

    public BdioOptions createBdioOptions() {
        String aggregateName = detectConfiguration.getProperty(DetectProperty.DETECT_BOM_AGGREGATE_NAME, PropertyAuthority.None);
        return new BdioOptions(aggregateName);

    }

    public ProjectNameVersionOptions createProjectNameVersionOptions(String sourceDirectoryName) {
        String projectBomTool = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_NAME, PropertyAuthority.None);
        String overrideProjectName = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_NAME, PropertyAuthority.None);
        String overrideProjectVersionName = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_VERSION_NAME, PropertyAuthority.None);
        String defaultProjectVersionText = detectConfiguration.getProperty(DetectProperty.DETECT_DEFAULT_PROJECT_VERSION_TEXT, PropertyAuthority.None);
        String defaultProjectVersionScheme = detectConfiguration.getProperty(DetectProperty.DETECT_DEFAULT_PROJECT_VERSION_SCHEME, PropertyAuthority.None);
        String defaultProjectVersionFormat = detectConfiguration.getProperty(DetectProperty.DETECT_DEFAULT_PROJECT_VERSION_TIMEFORMAT, PropertyAuthority.None);
        return new ProjectNameVersionOptions(sourceDirectoryName, projectBomTool, overrideProjectName, overrideProjectVersionName, defaultProjectVersionText, defaultProjectVersionScheme, defaultProjectVersionFormat);
    }

    public DetectProjectServiceOptions createDetectProjectServiceOptions() {
        final String projectVersionPhase = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_VERSION_PHASE, PropertyAuthority.None);
        final String projectVersionDistribution = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_VERSION_DISTRIBUTION, PropertyAuthority.None);
        final Integer projectTier = detectConfiguration.getIntegerProperty(DetectProperty.DETECT_PROJECT_TIER, PropertyAuthority.None);
        final String projectDescription = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_DESCRIPTION, PropertyAuthority.None);
        final String projectVersionNotes = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_VERSION_NOTES, PropertyAuthority.None);
        final String[] cloneCategories = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_PROJECT_CLONE_CATEGORIES, PropertyAuthority.None);
        final Boolean projectLevelAdjustments = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_PROJECT_LEVEL_ADJUSTMENTS, PropertyAuthority.None);
        final Boolean forceProjectVersionUpdate = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_PROJECT_VERSION_UPDATE, PropertyAuthority.None);
        final String cloneVersionName = detectConfiguration.getProperty(DetectProperty.DETECT_CLONE_PROJECT_VERSION_NAME, PropertyAuthority.None);
        return new DetectProjectServiceOptions(projectVersionPhase, projectVersionDistribution, projectTier, projectDescription, projectVersionNotes, cloneCategories, projectLevelAdjustments, forceProjectVersionUpdate, cloneVersionName);
    }

    public BlackDuckSignatureScannerOptions createBlackDuckSignatureScannerOptions() {
        final String[] signatureScannerPaths = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PATHS, PropertyAuthority.None);
        final String[] exclusionPatterns = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_PATTERNS, PropertyAuthority.None);
        final String[] exclusionNamePatterns = detectConfiguration.getStringArrayProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_EXCLUSION_NAME_PATTERNS, PropertyAuthority.None);

        final Integer scanMemory = detectConfiguration.getIntegerProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_MEMORY, PropertyAuthority.None);
        final Integer parrallelProcessors = detectConfiguration.getIntegerProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_PARALLEL_PROCESSORS, PropertyAuthority.None);
        final Boolean cleanupOutput = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_CLEANUP, PropertyAuthority.None);
        final Boolean dryRun = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_DRY_RUN, PropertyAuthority.None);
        final Boolean snippetMatching = detectConfiguration.getBooleanProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_SNIPPET_MODE, PropertyAuthority.None);
        final String codeLocationPrefix = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_PREFIX, PropertyAuthority.None);
        final String codeLocationSuffix = detectConfiguration.getProperty(DetectProperty.DETECT_PROJECT_CODELOCATION_SUFFIX, PropertyAuthority.None);
        final String additionalArguments = detectConfiguration.getProperty(DetectProperty.DETECT_BLACKDUCK_SIGNATURE_SCANNER_ARGUMENTS, PropertyAuthority.None);

        return new BlackDuckSignatureScannerOptions(signatureScannerPaths, exclusionPatterns, exclusionNamePatterns, scanMemory, parrallelProcessors, cleanupOutput, dryRun,
            snippetMatching, codeLocationPrefix, codeLocationSuffix, additionalArguments);
    }
}