//package network;
//
//import entity.Synchronize;
//
//import java.lang.reflect.Field;
//import java.util.ArrayList;
//import java.util.Arrays;
//
//public class FieldChangeSystem {
//    public class FieldValue {
//        public Object obj;
//        public Field field;
//        public Object value;
//
//        public FieldValue(Object obj, Field field) {
//            this.obj = obj;
//            this.field = field;
//            this.value = null;
//        }
//
//        public FieldValue(Object obj, Field field, Object value) {
//            this.obj = obj;
//            this.field = field;
//            this.value = value;
//        }
//    }
//
//    public interface FieldChangeListener {
//        void onFieldChange(FieldValue n);
//    }
//
//    private final ArrayList<FieldValue> fields;
//    private final FieldChangeListener listener;
//
//    public FieldChangeSystem(Changeable[] objs, FieldChangeListener listener) {
//        this.listener = listener;
//
//        this.fields = new ArrayList<>();
//        Arrays.stream(objs).forEach(this::addObject);
//    }
//
//    public void addObject(Changeable obj) {
//		Arrays.stream(obj.getClass().getFields()).filter(f -> f.getAnnotation(Synchronize.class) != null).forEach(f -> {
//			try {
//				fields.add(new FieldValue(obj, f, f.get(obj)));
//			} catch (IllegalAccessException e) {
//				e.printStackTrace();
//			}
//		});
//    }
//
//    public void update() {
//        fields.stream().filter(f -> {
//            try {
//                Object value = f.field.get(f.obj);
//                boolean changed = !f.value.equals(value);
//                f.value = value;
//                return changed;
//            } catch (IllegalAccessException e) {
//                e.printStackTrace();
//            }
//            return false;
//        }).forEach(listener::onFieldChange);
//    }
//
//    public static void main(String[] args) {
//    }
//}