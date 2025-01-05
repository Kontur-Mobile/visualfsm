package ru.kontur.mobile.visualfsm.annotation_processor

import annotation_processor.AnnotationProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.jetbrains.kotlin.compiler.plugin.ExperimentalCompilerApi
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test

@OptIn(ExperimentalCompilerApi::class)
internal class FeatureErrorAnnotationProcessorTests {

    @Test
    fun testClassWithUsesGeneratedTransitionsFactoryAnnotationNotInheritedFromFeature() {
        val testFSMSource = SourceFile.kotlin(
            name = "Test.kt",
            contents = """
                import ru.kontur.mobile.visualfsm.*
                import ru.kontur.mobile.visualfsm.tools.GeneratedTransitionsFactoryFunctionProvider.provideTransitionsFactoryFunction
                
                sealed class TestState: State {
                    class TestState1: TestState()
                    class TestState2: TestState()
                }
                
                sealed class TestAction: Action<TestState>()
                
                class TestAction1(val parameter1: String): TestAction() {
                
                    inner class Transition1: Transition<TestState.TestState1, TestState.TestState2>() {
                        override fun transform(state: TestState.TestState1): TestState.TestState2 = TestState.TestState2()
                    }
                
                    inner class Transition2: Transition<TestState.TestState2, TestState.TestState1>() {
                        override fun transform(state: TestState.TestState2): TestState.TestState1 = TestState.TestState1()
                    }
                
                }
                
                @GenerateTransitionsFactory
                class TestFeature
                """
        )

        val compilation = KotlinCompilation().apply {
            sources = TestUtil.getVisualFSMSources() + testFSMSource
            symbolProcessorProviders = listOf(AnnotationProcessorProvider())
        }
        val result = compilation.compile()
        Assertions.assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        Assertions.assertTrue(result.messages.contains("Only class inherited from class ru.kontur.mobile.visualfsm.Feature or class ru.kontur.mobile.visualfsm.rxjava3.FeatureRx can be annotated with @ru.kontur.mobile.visualfsm.GenerateTransitionsFactory. The \"TestFeature(Test.kt:24)\" does not meet this requirement."))
    }

    @Test
    fun testFeatureMustHaveSuperClassWithTwoGenericParams() {
        val testFSMSource = SourceFile.kotlin(
            name = "Test.kt",
            contents = """
                import ru.kontur.mobile.visualfsm.*
                import ru.kontur.mobile.visualfsm.tools.GeneratedTransitionsFactoryFunctionProvider.provideTransitionsFactoryFunction
                
                sealed class TestState: State {
                    class TestState1: TestState()
                    class TestState2: TestState()
                }
                
                sealed class TestAction: Action<TestState>()
                
                class TestAction1(val parameter1: String): TestAction() {
                
                    inner class Transition1: Transition<TestState.TestState1, TestState.TestState2>() {
                        override fun transform(state: TestState.TestState1): TestState.TestState2 = TestState.TestState2()
                    }
                
                    inner class Transition2: Transition<TestState.TestState2, TestState.TestState1>() {
                        override fun transform(state: TestState.TestState2): TestState.TestState1 = TestState.TestState1()
                    }
                
                }
                
                abstract class FeatureAbstract(
                    initialState: TestState,
                    asyncWorker: AsyncWorker<TestState, TestAction>? = null,
                    transitionCallbacks: TransitionCallbacks<TestState>? = null
                ): Feature<TestState, TestAction>(
                    initialState = TestState.TestState1(),
                    asyncWorker = asyncWorker,
                    transitionCallbacks = transitionCallbacks,
                    transitionsFactory = provideTransitionsFactory(),
                )
                
                @GenerateTransitionsFactory
                class TestFeature: FeatureAbstract(
                    initialState = TestState.TestState1(),
                )
                """
        )

        val compilation = KotlinCompilation().apply {
            sources = TestUtil.getVisualFSMSources() + testFSMSource
            symbolProcessorProviders = listOf(AnnotationProcessorProvider())
        }
        val result = compilation.compile()
        Assertions.assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        Assertions.assertTrue(result.messages.contains("Super class of feature must have exactly two generic types (state and action). But the super class of \"TestFeature(Test.kt:35)\" has 0: []"))
    }

    @Test
    fun testFeatureMustHaveSuperClassWithBaseStateGenericParameter() {
        val testFSMSource = SourceFile.kotlin(
            name = "Test.kt",
            contents = """
                import ru.kontur.mobile.visualfsm.*
                import ru.kontur.mobile.visualfsm.tools.GeneratedTransitionsFactoryFunctionProvider.provideTransitionsFactoryFunction
                
                sealed class TestState: State {
                    class TestState1: TestState()
                    class TestState2: TestState()
                }
                
                sealed class TestAction: Action<TestState>()
                
                class TestAction1(val parameter1: String): TestAction() {
                
                    inner class Transition1: Transition<TestState.TestState1, TestState.TestState2>() {
                        override fun transform(state: TestState.TestState1): TestState.TestState2 = TestState.TestState2()
                    }
                
                    inner class Transition2: Transition<TestState.TestState2, TestState.TestState1>() {
                        override fun transform(state: TestState.TestState2): TestState.TestState1 = TestState.TestState1()
                    }
                
                }
                
                abstract class FeatureAbstract<ACTION: Action<*>, T>(
                    initialState: TestState,
                    asyncWorker: AsyncWorker<TestState, TestAction>? = null,
                    transitionCallbacks: TransitionCallbacks<TestState>? = null
                ): Feature<TestState, TestAction>(
                    initialState = TestState.TestState1(),
                    asyncWorker = asyncWorker,
                    transitionCallbacks = transitionCallbacks,
                    transitionsFactory = provideTransitionsFactory(),
                )
                
                @GenerateTransitionsFactory
                class TestFeature: FeatureAbstract<TestAction, Any>(
                    initialState = TestState.TestState1(),
                )
                """
        )

        val compilation = KotlinCompilation().apply {
            sources = TestUtil.getVisualFSMSources() + testFSMSource
            symbolProcessorProviders = listOf(AnnotationProcessorProvider())
        }
        val result = compilation.compile()
        Assertions.assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        Assertions.assertTrue(result.messages.contains("Super class of feature must have base state as one of two generic types. The \"TestFeature(Test.kt:35)\" does not meet this requirement."))
    }

    @Test
    fun testFeatureMustHaveSuperClassWithBaseActionGenericParameter() {
        val testFSMSource = SourceFile.kotlin(
            name = "Test.kt",
            contents = """
                import ru.kontur.mobile.visualfsm.*
                import ru.kontur.mobile.visualfsm.tools.GeneratedTransitionsFactoryFunctionProvider.provideTransitionsFactoryFunction
                
                sealed class TestState: State {
                    class TestState1: TestState()
                    class TestState2: TestState()
                }
                
                sealed class TestAction: Action<TestState>()
                
                class TestAction1(val parameter1: String): TestAction() {
                
                    inner class Transition1: Transition<TestState.TestState1, TestState.TestState2>() {
                        override fun transform(state: TestState.TestState1): TestState.TestState2 = TestState.TestState2()
                    }
                
                    inner class Transition2: Transition<TestState.TestState2, TestState.TestState1>() {
                        override fun transform(state: TestState.TestState2): TestState.TestState1 = TestState.TestState1()
                    }
                
                }
                
                abstract class FeatureAbstract<STATE: State, T>(
                    initialState: TestState,
                    asyncWorker: AsyncWorker<TestState, TestAction>? = null,
                    transitionCallbacks: TransitionCallbacks<TestState>? = null
                ): Feature<TestState, TestAction>(
                    initialState = TestState.TestState1(),
                    asyncWorker = asyncWorker,
                    transitionCallbacks = transitionCallbacks,
                    transitionsFactory = provideTransitionsFactory(),
                )
                
                @GenerateTransitionsFactory
                class TestFeature: FeatureAbstract<TestState, Any>(
                    initialState = TestState.TestState1(),
                )
                """
        )

        val compilation = KotlinCompilation().apply {
            sources = TestUtil.getVisualFSMSources() + testFSMSource
            symbolProcessorProviders = listOf(AnnotationProcessorProvider())
        }
        val result = compilation.compile()
        Assertions.assertEquals(KotlinCompilation.ExitCode.COMPILATION_ERROR, result.exitCode)
        Assertions.assertTrue(result.messages.contains("Super class of feature must have base action as one of two generic types. The \"TestFeature(Test.kt:35)\" does not meet this requirement."))
    }

}