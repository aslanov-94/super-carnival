package az.ingress.k8s.client.util;

import javax.annotation.Nullable;
import java.util.function.Supplier;

public final class ObjectsUtil {

    public static <T> boolean nonNull(Supplier<T> supplier) {
        try {
            return supplier.get() != null;
        } catch (NullPointerException ex) {
            return false;
        }
    }

    @Nullable
    public static <T> T nullPointerSafeGet(Supplier<T> supplier) {
        try {
            return supplier.get();
        } catch (NullPointerException exception) {
            return null;
        }
    }

}
