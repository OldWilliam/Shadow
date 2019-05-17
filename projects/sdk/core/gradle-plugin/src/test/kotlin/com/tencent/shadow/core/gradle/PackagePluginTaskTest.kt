package com.tencent.shadow.core.gradle

import org.gradle.testkit.runner.GradleRunner
import org.gradle.testkit.runner.TaskOutcome.SUCCESS
import org.json.simple.JSONArray
import org.json.simple.JSONObject
import org.json.simple.parser.JSONParser
import org.junit.Assert
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Test
import java.io.File
import java.util.zip.ZipFile

/**
 *   测试打包包含loader、runtime、插件1、config.json的插件包
 *  ./gradlew -p projects/sdk/core :gradle-plugin:test --tests com.tencent.shadow.core.gradle.PackagePluginTaskTest.testCase1PackageDebugPlugin
 */
class PackagePluginTaskTest {

    @Test
    fun testCase1PackageDebugPlugin() {
        GradleRunner.create()
                .withProjectDir(ROOT_PROJECT_DIR)
                .withPluginClasspath()
                .withArguments("clean")
                .build()

        val result = GradleRunner.create()
                .withProjectDir(ROOT_PROJECT_DIR)
                .withPluginClasspath()
                .withArguments(listOf(
                        "-Pdisable_shadow_transform=true",
                        ":plugin1:packageDebugPlugin"
                ))
                .build()

        val outcome = result.task(":plugin1:packageDebugPlugin")!!.outcome

        assertEquals(SUCCESS, outcome)

        val jsonFile = File(PLUGIN1_PROJECT_DIR, "build/intermediates/generatePluginConfig/debug/config.json")
        val json = JSONParser().parse(jsonFile.bufferedReader()) as JSONObject
        assertJson(json)

        val zipFile = ZipFile(ROOT_PROJECT_DIR.absolutePath + "/build/plugin-debug.zip")
        assertFile(zipFile)
    }

    private fun assertFile(zipFile: ZipFile) {
        val zipFileNames = mutableSetOf<String>()
        zipFileNames.add("config.json")
        zipFileNames.add("plugin1-debug.apk")
        zipFileNames.add("loader-debug.apk")
        zipFileNames.add("runtime-debug.apk")

        val entries = zipFile.entries()
        assertEquals(4, zipFile.size())

        for (i in entries) {
            zipFileNames.remove(i.name)
        }
        assertEquals(0, zipFileNames.size)

    }

    private fun assertJson(json: JSONObject) {
        assertEquals(4L, json["version"])

        assertEquals("1234567890", json["UUID"])

        assertEquals("1.1.5", json["UUID_NickName"])

        val compactVersionArr: JSONArray = json["compact_version"] as JSONArray
        assertEquals(1L, compactVersionArr[0] as Long)

        val loaderJson = json["pluginLoader"] as JSONObject
        assertEquals("loader-debug.apk", loaderJson["apkName"])
        assertNotNull(loaderJson["hash"])

        val runtimeJson = json["runtime"] as JSONObject
        assertEquals("runtime-debug.apk", runtimeJson["apkName"])
        assertNotNull(runtimeJson["hash"])

        val pluginsJson = json["plugins"] as JSONArray
        val pluginJson = pluginsJson[0] as JSONObject
        assertEquals("plugin1", pluginJson["partKey"])
        assertEquals("plugin1", pluginJson["businessName"])
        assertEquals("plugin1-debug.apk", pluginJson["apkName"])
        val dependsOnJson = pluginJson["dependsOn"] as JSONArray
        assertEquals(2, dependsOnJson.size)
        assertNotNull(pluginJson["hash"])

        val hostWhiteList = pluginJson["hostWhiteList"] as JSONArray
        Assert.assertEquals(2, hostWhiteList.size)
    }

    companion object {
        val ROOT_PROJECT_DIR = File("src/test/testProjects/case1")
        val PLUGIN1_PROJECT_DIR = File("src/test/testProjects/case1/plugin1")
    }
}