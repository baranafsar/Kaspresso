package com.kaspersky.kaspresso.sample_upgrade_tests.test

import android.Manifest
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.rule.GrantPermissionRule

import com.kaspersky.components.kautomator.KautomatorConfigurator
import com.kaspersky.kaspresso.sample_upgrade_tests.common.UpgradeTestCase
import com.kaspersky.kaspresso.sample_upgrade_tests.screen.MainScreen
import com.kaspersky.kaspresso.sample_upgrade_tests.screen.UpgradeScreen
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class UpgradeTestSample : UpgradeTestCase() {

    companion object {
        private const val VERSION_TEXT_BEFORE_UPDATE = "v.1"
        private const val VERSION_TEXT_AFTER_UPDATE = "v.2"
        private const val VALUE = "Kaspresso"
    }

    @get:Rule
    val runtimePermissionRule: GrantPermissionRule = GrantPermissionRule.grant(
        Manifest.permission.WRITE_EXTERNAL_STORAGE,
        Manifest.permission.READ_EXTERNAL_STORAGE
    )

    @Test
    fun upgradeTest() {
        before {
            KautomatorConfigurator {
                intercept {
                    onUiInteraction {
                        onCheck { uiInteraction, uiAssert ->
                            testLogger.i("UIA", "type=${uiAssert.type}, description=${uiAssert.description}")
                        }
                        onPerform { uiInteraction, uiAction ->
                            testLogger.i("UIA", "type=${uiAction.type}, description=${uiAction.description}")
                        }
                    }
                }
            }
        }.after {
            uninstallMainApp()
        }.run {

            step("Install and launch old version of the app") {
                installAndLaunchMainApp()
            }

            step("Click on upgrade scenario button and check version") {
                MainScreen {
                    upgradeButton {
                        click()
                    }
                }
            }

            step("Check version name") {
                UpgradeScreen {
                    version {
                        hasText(VERSION_TEXT_BEFORE_UPDATE)
                    }
                }
            }

            step("Update text value") {
                UpgradeScreen {
                    input {
                        replaceText(VALUE)
                    }
                    applyBtn {
                        click()
                    }
                    value {
                        containsText(VALUE)
                    }
                }
            }

            step("Press Home button") {
                device.uiDevice.pressHome()
            }

            step("Upgrade the app and launch then") {
                updateAndLaunchMainApp()
            }

            step("Click on upgrade scenario button") {
                MainScreen {
                    upgradeButton {
                        click()
                    }
                }
            }

            step("Check if value has been restored after update") {
                UpgradeScreen {
                    version {
                        hasText(VERSION_TEXT_AFTER_UPDATE)
                    }
                    value {
                        containsText(VALUE)
                    }
                }
            }
        }
    }
}
