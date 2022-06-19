package ru.kontur.mobile.visualfsm.annotation_processor

import annotation_processor.AnnotationProcessorProvider
import com.tschuchort.compiletesting.KotlinCompilation
import com.tschuchort.compiletesting.SourceFile
import com.tschuchort.compiletesting.symbolProcessorProviders
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test
import ru.kontur.mobile.visualfsm.annotation_processor.TestUtil.getKspGeneratedSources

internal class AnnotationProcessorTests {
    @Test
    fun testNoErrors() {
        val testActionSource = SourceFile.kotlin(
            name = "Test.kt",
            contents = """
                import ru.kontur.mobile.visualfsm.*
                import ru.kontur.mobile.visualfsm.tools.GeneratedTransactionFactoryProvider
                
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
                
                @UsesGeneratedTransactionFactory
                class TestFeature: Feature<TestState, TestAction>(
                    initialState = TestState.TestState1(),
                    transitionFactory = GeneratedTransactionFactoryProvider.provide()
                )
                """
        )

        val compilation = KotlinCompilation().apply {
            sources = TestUtil.getVisualFSMSources() + testActionSource
            symbolProcessorProviders = listOf(AnnotationProcessorProvider())
        }
        val result = compilation.compile()
        Assertions.assertEquals(KotlinCompilation.ExitCode.OK, result.exitCode)
        val kspGeneratedSources = result.getKspGeneratedSources()
        val generatedTestStateTransactionFactory = kspGeneratedSources.first { it.path.endsWith("GeneratedTestStateTransactionFactory.kt") }
        println(generatedTestStateTransactionFactory.readText())
        Assertions.assertEquals(
            "import ru.kontur.mobile.visualfsm.TransitionFactory\n" +
                    "\n" +
                    "public class GeneratedTestStateTransactionFactory : TransitionFactory<TestState, TestAction> {\n" +
                    "  public override fun create(action: TestAction) = when (action) {\n" +
                    "      is TestAction1 -> listOf(\n" +
                    "          action.Transition1().apply {\n" +
                    "              fromState = TestState.TestState1::class\n" +
                    "              toState = TestState.TestState2::class\n" +
                    "          },\n" +
                    "          action.Transition2().apply {\n" +
                    "              fromState = TestState.TestState2::class\n" +
                    "              toState = TestState.TestState1::class\n" +
                    "          },\n" +
                    "      )\n" +
                    "      else -> error(\"All sealed subclasses of base Action must be handled in when\")\n" +
                    "  }\n" +
                    "\n" +
                    "}\n",
            generatedTestStateTransactionFactory.readText()
        )
    }
}