/**
 * 归并排序
 *
 * @author Runner
 * @version 1.0
 * @since 2024/8/21 16:37
 */
public class Demo {
    public static void main(String[] args) {

//        List<SQLStatement> sqlStatements = SQLUtils.parseStatements("Select id,name from t where id = 1 ", JdbcConstants.MYSQL);


        int[] arrays = new int[]{1,5,4,3,6,0,3,9,5,3,4};
        sort(arrays);
        for (int array : arrays) {
            System.out.println(array);
        }
    }

    private static int[] sort(int[] arrays) {
       mergeSort(arrays, 0, arrays.length - 1);
       return arrays;
    }

    private static void mergeSort(int[] arrays, int left, int right) {
        if (left >= right) {
            return;
        }
        // todo 考虑正数溢出的问题
        int middle = (right + left) / 2;
        mergeSort(arrays, left, middle);
        mergeSort(arrays, middle + 1, right);
        merge(arrays, left, middle, right);
    }

    private static void merge(int[] arrays, int left, int middle, int right) {
        int i = left;
        int j = middle +1;
        int[] temp = new int[right- left + 1];
        int tempIndex=0;

        while (i < j && j < right + 1 && i < middle + 1) {
            if (arrays[i] < arrays[j]) {
                temp[tempIndex++] = arrays[i++];
            }else {
                temp[tempIndex++] = arrays[j++];
            }
        }

        if (i < j) {
            for (int m =i ;m<middle+1; m++) {
                temp[tempIndex++] = arrays[m];
            }
        }

        if (j < right + 1) {
            for (int m =j ;m<=right; m++) {
                temp[tempIndex++] = arrays[m];

            }
        }

        int tempIdx = 0;
        for (int m = left; m<= right; m++) {
            arrays[m] = temp[tempIdx++];
        }
    }
}
