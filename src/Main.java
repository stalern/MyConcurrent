/**
 * @author stalern
 * @date 2019年9月16日17:14:18
 * 主函数
 */
public class Main {

    public static void main(String[] args) {
        MySynchronized mySynchronized = new MySynchronized();
        mySynchronized.writer(10);
        System.out.println(mySynchronized.reader());
    }
}
