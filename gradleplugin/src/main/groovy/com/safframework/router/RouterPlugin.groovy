package  com.safframework.router

import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.ProjectConfigurationException

/**
 * 实现Plugin
 */
public class RouterPlugin implements Plugin<Project>{

    @Override
    void apply(Project project) {

        if (!hasAndroidPlugin(project) && !hasAndroidLibraryPlugin(project)) {
            throw new ProjectConfigurationException("the gradle plugin can only be applied to android projects.", null)
        }

        // 添加插件
        project.plugins.apply("com.neenbedankt.android-apt")

        project.dependencies {
            compile 'com.safframework.router:saf-router:1.0.0'
            apt 'com.safframework.router:saf-router-compiler:1.0.2'
        }
    }

    /**
     * 判断是否有com.android.application插件
     * @param project
     * @return
     */
    static boolean hasAndroidPlugin(Project project) {
        return project.plugins.hasPlugin("com.android.application")
    }

    /**
     * 判断是否有com.android.library插件
     * @param project
     * @return
     */
    static boolean hasAndroidLibraryPlugin(Project project) {
        return project.plugins.hasPlugin("com.android.library")
    }
}