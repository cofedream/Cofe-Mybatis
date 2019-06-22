package tk.cofe.plugin.mybatis.constants;

import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.psi.ResolveResult;

/**
 * @author : zhengrf
 * @date : 2019-06-16
 */
public final class Empty {
    private Empty() {
    }

    public static final String STRING = "";

    public static final class Array {
        public static final ResolveResult[] RESOLVE_RESULT = new ResolveResult[0];
        public static final String[] STRING = new String[0];
        public static final ProblemDescriptor[] PROBLEM_DESCRIPTOR = new ProblemDescriptor[0];
    }
}
