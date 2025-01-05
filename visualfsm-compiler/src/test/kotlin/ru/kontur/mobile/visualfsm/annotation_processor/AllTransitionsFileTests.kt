package ru.kontur.mobile.visualfsm.annotation_processor

import annotation_processor.AnnotationProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.kspArgs
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.annotation_processor.TestUtil.getKspNoCodeGeneratedSources

@OptIn(ExperimentalCompilerApi::class)
internal class AllTransitionsFileTests {
    @Test
    fun testAllTransitionsFile() {
        val testFSMSource = SourceFile.kotlin(
            name = "Test.kt",
            contents = """
                import ru.kontur.mobile.visualfsm.*
                import ru.kontur.mobile.visualfsm.tools.GeneratedTransitionsFactoryFunctionProvider.provideTransitionsFactoryFunction
                
                sealed class TestState: State {
                    class TestState1: TestState()
                    class TestState2: TestState()
                    class TestState3: TestState()
                }
                
                sealed class TestAction: Action<TestState>()
                
                class TestAction1(val parameter1: String): TestAction() {
                
                    inner class Transition11: Transition<TestState.TestState1, TestState.TestState2>() {
                        override fun transform(state: TestState.TestState1): TestState.TestState2 = TestState.TestState2()
                    }
                
                    inner class Transition12: Transition<TestState.TestState2, TestState.TestState1>() {
                        override fun transform(state: TestState.TestState2): TestState.TestState1 = TestState.TestState1()
                    }
                
                }

                class TestAction2(val parameter1: String): TestAction() {
                
                    inner class Transition21: Transition<TestState.TestState1, TestState.TestState2>() {
                        override fun transform(state: TestState.TestState1): TestState.TestState2 = TestState.TestState2()
                    }
                
                    inner class Transition22: Transition<TestState.TestState2, TestState.TestState1>() {
                        override fun transform(state: TestState.TestState2): TestState.TestState1 = TestState.TestState1()
                    }
                
                    inner class Transition23: Transition<TestState.TestState3, TestState.TestState2>() {
                        override fun transform(state: TestState.TestState1): TestState.TestState2 = TestState.TestState2()
                    }
                
                    @Edge("NamedByEdgeTransition")
                    inner class Transition24: Transition<TestState.TestState2, TestState.TestState3>() {
                        override fun transform(state: TestState.TestState2): TestState.TestState1 = TestState.TestState1()
                    }
                
                }
                
                @GenerateTransitionsFactory
                class TestFeature: Feature<TestState, TestAction>(
                    initialState = TestState.TestState1(),
                    transitionsFactory = provideTransitionsFactory(),
                )
                """
        )

        val compilation = KotlinCompilation().apply {
            sources = TestUtil.getVisualFSMSources() + testFSMSource
            symbolProcessorProviders = listOf(AnnotationProcessorProvider())
            kspArgs = mutableMapOf(
                "generateAllTransitionsCsvFiles" to "true",
            )
        }
        val result = compilation.compile()
        Assertions.assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val kspGeneratedSources = result.getKspNoCodeGeneratedSources()
        val generatedAllTransitionsFile = kspGeneratedSources.first { it.path.endsWith("TestStateAllTransitions.csv") }
        println(generatedAllTransitionsFile.readText())
        Assertions.assertEquals(
            "Transition11,TestState1,TestState2\n" +
                "Transition12,TestState2,TestState1\n" +
                "Transition21,TestState1,TestState2\n" +
                "Transition22,TestState2,TestState1\n" +
                "Transition23,TestState3,TestState2\n" +
                "NamedByEdgeTransition,TestState2,TestState3",
            generatedAllTransitionsFile.readText()
        )
    }

    @Test
    fun testTransitionsWithSealedClassInFromStateFile() {
        val testFSMSource = SourceFile.kotlin(
            name = "Test.kt",
            contents = """
                import ru.kontur.mobile.visualfsm.*
                import ru.kontur.mobile.visualfsm.tools.GeneratedTransitionsFactoryFunctionProvider.provideTransitionsFactoryFunction
                
                sealed class TestState: State {
                    class TestState1: TestState()

                    sealed class TestNavigation: TestState(){
                        data object NavigateToExit:  TestNavigation()                      
                        data object NavigateToNext:  TestNavigation()

                        sealed class DialogNavigation: TestNavigation(){
                            data object Show: DialogNavigation()
                            data object Hide: DialogNavigation()
                        }
                    }
                }
                
                sealed class TestAction: Action<TestState>()
                
                class TestAction1(val parameter1: String): TestAction() {
                
                    inner class Transition1: Transition<TestState.TestNavigation, TestState.TestState1>() {
                        override fun transform(state: TestState.TestNavigation): TestState.TestState1 = TestState.TestState1()
                    }

                    inner class Transition2: Transition<TestState.TestState1, TestState.TestNavigation.DialogNavigation.Show>() {
                        override fun transform(state: TestState.TestState1): TestState.TestNavigation.DialogNavigation.Show = TestState.TestNavigation.DialogNavigation.Show()
                    }

                    inner class Transition3: Transition<TestState.TestNavigation.DialogNavigation.Show, TestState.TestNavigation.DialogNavigation.Hide>() {
                        override fun transform(state: TestNavigation.DialogNavigation.Show): TestState.TestNavigation.DialogNavigation.Hide = TestState.TestNavigation.DialogNavigation.Hide()
                    }
                }
                
                @GenerateTransitionsFactory
                class TestFeature: Feature<TestState, TestAction>(
                    initialState = TestState.TestState1(),
                    transitionsFactory = provideTransitionsFactory(),
                )
                """
        )

        val compilation = KotlinCompilation().apply {
            sources = TestUtil.getVisualFSMSources() + testFSMSource
            symbolProcessorProviders = listOf(AnnotationProcessorProvider())
            kspArgs = mutableMapOf(
                "generateAllTransitionsCsvFiles" to "true",
            )
        }
        val result = compilation.compile()
        Assertions.assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val kspGeneratedSources = result.getKspNoCodeGeneratedSources()
        val generatedAllTransitionsFile = kspGeneratedSources.first { it.path.endsWith("TestStateAllTransitions.csv") }
        println(generatedAllTransitionsFile.readText())
        Assertions.assertEquals(
            "Transition1,TestNavigation.DialogNavigation.Hide,TestState1\n" +
                "Transition1,TestNavigation.DialogNavigation.Show,TestState1\n" +
                "Transition1,TestNavigation.NavigateToExit,TestState1\n" +
                "Transition1,TestNavigation.NavigateToNext,TestState1\n" +
                "Transition2,TestState1,TestNavigation.DialogNavigation.Show\n" +
                "Transition3,TestNavigation.DialogNavigation.Show,TestNavigation.DialogNavigation.Hide",
            generatedAllTransitionsFile.readText()
        )
    }

    @Test
    fun testTransitionsWithSealedClassInFromStateAndToStateFile() {
        val testFSMSource = SourceFile.kotlin(
            name = "Test.kt",
            contents = """
                import ru.kontur.mobile.visualfsm.*
                import ru.kontur.mobile.visualfsm.tools.GeneratedTransitionsFactoryFunctionProvider.provideTransitionsFactoryFunction
                
                sealed class TestState: State {
                    class TestState1: TestState()

                    sealed class SealedTest1: TestState(){
                        data object TestState2:  SealedTest1()                      
                        data object TestState3:  SealedTest1()
                    }
                }
                
                sealed class TestAction: Action<TestState>()
                
                class TestAction1(val parameter1: String): TestAction() {
                
                    inner class Transition1: Transition<TestState.SealedTest1, TestState.SealedTest1>() {
                        override fun transform(state: TestState.SealedTest1): TestState.SealedTest1 = state
                    }

                    inner class Transition2: Transition<TestState.SealedTest1, TestState.TestState1>() {
                        override fun transform(state: TestState.SealedTest1): TestState.TestState1 = TestState.TestState1()
                    }
                }
                
                @GenerateTransitionsFactory
                class TestFeature: Feature<TestState, TestAction>(
                    initialState = TestState.TestState1(),
                    transitionsFactory = provideTransitionsFactory(),
                )
                """
        )

        val compilation = KotlinCompilation().apply {
            sources = TestUtil.getVisualFSMSources() + testFSMSource
            symbolProcessorProviders = listOf(AnnotationProcessorProvider())
            kspArgs = mutableMapOf(
                "generateAllTransitionsCsvFiles" to "true",
            )
        }
        val result = compilation.compile()
        Assertions.assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val kspGeneratedSources = result.getKspNoCodeGeneratedSources()
        val generatedAllTransitionsFile = kspGeneratedSources.first { it.path.endsWith("TestStateAllTransitions.csv") }
        println(generatedAllTransitionsFile.readText())
        Assertions.assertEquals(
            "Transition1,SealedTest1.TestState2,SealedTest1.TestState2\n" +
                "Transition1,SealedTest1.TestState2,SealedTest1.TestState3\n" +
                "Transition1,SealedTest1.TestState3,SealedTest1.TestState2\n" +
                "Transition1,SealedTest1.TestState3,SealedTest1.TestState3\n" +
                "Transition2,SealedTest1.TestState2,TestState1\n" +
                "Transition2,SealedTest1.TestState3,TestState1",
            generatedAllTransitionsFile.readText()
        )
    }

    @Test
    fun testTransitionsWithSealedClassAllGenericCombinationFile() {
        val testFSMSource = SourceFile.kotlin(
            name = "Test.kt",
            contents = """
                import ru.kontur.mobile.visualfsm.*
                import ru.kontur.mobile.visualfsm.tools.GeneratedTransitionsFactoryFunctionProvider.provideTransitionsFactoryFunction
                
                sealed class TestState: State {
                    class TestState1: TestState()
                    class TestState2(
                        val sealedState: SealedState
                    ): TestState()
                        
                    sealed class SealedState: TestState(){
                        data object SealedState1: SealedState()
                        data object SealedState2: SealedState()
                    }
                }
                
                internal sealed class TestAction: Action<TestState>()
                
                class TestAction1(val parameter1: String): TestAction() {
                    
                    inner class Transition1: Transition<TestState.SealedState, TestState.TestState1>() {
                        override fun transform(state: TestState.SealedState): TestState.TestState1 = TestState.TestState1()
                    }

                    inner class Transition2: Transition<TestState.SealedState, TestState.SealedState>() {
                        override fun transform(state: TestState.SealedState): TestState.SealedState = state
                    }

                    inner class Transition3: SelfTransition<TestState.SealedState>() {
                        override fun transform(state: TestState.SealedState): TestState.SealedState = state
                    }

                    inner class Transition4: Transition<TestState.TestState2, TestState.SealedState>() {
                        override fun transform(state: TestState.TestState2): TestState.SealedState = state.sealedState
                    }
                }
                
                @GenerateTransitionsFactory
                class TestFeature: Feature<TestState, TestAction>(
                    initialState = TestState.TestState1(),
                    transitionsFactory = provideTransitionsFactory(),
                )
                """
        )

        val compilation = KotlinCompilation().apply {
            sources = TestUtil.getVisualFSMSources() + testFSMSource
            symbolProcessorProviders = listOf(AnnotationProcessorProvider())
            kspArgs = mutableMapOf(
                "generateAllTransitionsCsvFiles" to "true",
            )
        }
        val result = compilation.compile()
        Assertions.assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val kspGeneratedSources = result.getKspNoCodeGeneratedSources()
        val generatedAllTransitionsFile = kspGeneratedSources.first { it.path.endsWith("TestStateAllTransitions.csv") }
        println(generatedAllTransitionsFile.readText())
        Assertions.assertEquals(
            "Transition1,SealedState.SealedState1,TestState1\n" +
                "Transition1,SealedState.SealedState2,TestState1\n" +
                "Transition2,SealedState.SealedState1,SealedState.SealedState1\n" +
                "Transition2,SealedState.SealedState1,SealedState.SealedState2\n" +
                "Transition2,SealedState.SealedState2,SealedState.SealedState1\n" +
                "Transition2,SealedState.SealedState2,SealedState.SealedState2\n" +
                "Transition3,SealedState.SealedState1,SealedState.SealedState1\n" +
                "Transition3,SealedState.SealedState2,SealedState.SealedState2\n" +
                "Transition4,TestState2,SealedState.SealedState1\n" +
                "Transition4,TestState2,SealedState.SealedState2",
            generatedAllTransitionsFile.readText()
        )
    }
}