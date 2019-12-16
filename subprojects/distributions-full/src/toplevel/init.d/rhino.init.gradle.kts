/*
 * Copyright 2020 the original author or authors.
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

logger.lifecycle("You are running a Metaswitch SEG build of gradle")

settingsEvaluated {
    fun getRepoHost(): String {
        return try {
            val repoHost: String by settings
            repoHost
        } catch (e: InvalidUserCodeException) {
            throw NoSuchElementException("Property 'repoHost' must be set (usually in ${settings.gradle.gradleUserHomeDir}/gradle.properties)")
        }
    }
    fun getRepoUsername(): String {
        return try {
            val repoUsername: String by settings
            repoUsername
        } catch (e: InvalidUserCodeException) {
            throw NoSuchElementException("Property 'repoUsername' must be set (usually in ${settings.gradle.gradleUserHomeDir}/gradle.properties)")
        }
    }
    fun getRepoPassword(): String {
        return try {
            val repoPassword: String by settings
            repoPassword
        } catch (e: InvalidUserCodeException) {
            throw NoSuchElementException("Property 'repoPassword' must be set (usually in ${settings.gradle.gradleUserHomeDir}/gradle.properties)")
        }
    }

    val repoHost: String = getRepoHost()
    val repoUsername: String = getRepoUsername()
    val repoPassword: String = getRepoPassword()
    val localRepoUri = uri("file:///${gradle.gradleUserHomeDir}/ivy-local")

    fun getRepoUri(): String {
        return try {
            //Allow the full uri to be overridden if present
            val repoUri: String by settings
            repoUri
        } catch (e: InvalidUserCodeException) {
            "https://$repoHost/artifactory"
        }
    }
    val repoUri: String = getRepoUri()

    settings.pluginManagement {
        repositories {
            ivy {
                name = "Local"
                url = localRepoUri
            }
            ivy {
                name = "Artifactory"
                url = uri("$repoUri/gradle-plugins")
                credentials {
                    username = repoUsername
                    password = repoPassword
                }
            }
            gradlePluginPortal()
        }
    }

    val isCiServer = System.getenv().containsKey("BUILD_NUMBER")
    buildCache {
        local {
            isEnabled = !isCiServer
            logger.info("Local build cache enabled: $isEnabled")
        }
        remote<HttpBuildCache> {
            url = uri("$repoUri/gradle-buildcache/")
            credentials {
                username = repoUsername
                password = repoPassword
            }
            isPush = isCiServer
            logger.info("Using remote build cache: $url")
            logger.info("  With push enabled: $isPush")
        }
    }
}
