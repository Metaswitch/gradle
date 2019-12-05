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
    val repoHost: String by settings
    val repoUsername: String by settings
    val repoPassword: String by settings
    val localRepoUri = uri("file:///${gradle.gradleUserHomeDir}/ivy-local")

    fun getRepoUri(): String {
        try {
            //Allow the full uri to be overridden if present
            val repoUri: String by settings
            return repoUri
        } catch (e: InvalidUserCodeException) {
            return "https://$repoHost/artifactory"
        }
    }
    var uri: String = getRepoUri()

    settings.pluginManagement {
        repositories {
            ivy {
                name = "Local"
                url = localRepoUri
            }
            ivy {
                name = "Artifactory"
                url = uri("$uri/gradle-plugins")
                credentials {
                    username = repoUsername
                    password = repoPassword
                }
            }
            gradlePluginPortal()
        }
    }
}
