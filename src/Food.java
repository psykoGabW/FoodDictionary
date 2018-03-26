import java.util.Iterator;
import java.util.zip.DataFormatException;

class Food {

    public enum eFoodAttributes {

        NAME("Name"),
        CATEGORY("Category"),
        ENERGETIC_VALUE("Energetic value (kcal)"),
        PROTEIN_RATE("Protein rate (g/100g)"),
        GLUCID_RATE("Glucid rate (g/100g)"),
        LIPID_RATE("Lipid rate (g/100g)");

        private String attributeLbl = "";

        eFoodAttributes(String attributeLbl) {
            this.attributeLbl = attributeLbl;

        }

        public String toString() {
            return attributeLbl;
        }

    }

/*    private final static String[] ATTRIBUTE_NAMES = {"Name", "Category", "Energetic value (kcal)",
            "Protein rate (g/100g)", "Glucid rate (g/100g)", "Lipid rate (g/100g)"};
*/

    private static final String FORMAT_TO_STRING = "[ Name : %s | Category : %s | Energetic value : %d(kcal) | Protein rate : %s(g/100g) | Glucid rate : %s(g/100g) | Lipid rate : %s(g/100g) ]";

    public final static String DATA_SEPARATOR = ";";

    private final static int NB_OF_ATTRIBUTES = 6;

    private String name = "";
    private String category = "";
    private int energeticValue = -1; // kcal
    private int proteinRate = -1; // x g/100g  format : xxx.yy -> store xx xyy
    private int glucidRate = -1; // x g/100g format : xxx.yy
    private int lipidRate = -1;  // x g/100g format : xxx.yy

    private static int setRate(String formattedRate) {
        return (Math.round(100 * (Float.parseFloat(formattedRate))));
    }

    private static String getFormattedRate(int rate) {
        return (String.format("%d.%02d", rate / 100, rate % 100));
    }

    public void initFromFileLine(String rowData) {
        String[] rowDatas = rowData.split(DATA_SEPARATOR, -1);

        // To add : Raise an exception if rowDatas size > NB_OF_ATTRIBUTES
        name = rowDatas[0];
        category = rowDatas[1];
        energeticValue = Integer.parseInt(rowDatas[2]);

        proteinRate = setRate(rowDatas[3]);
        glucidRate = setRate(rowDatas[4]);
        lipidRate = setRate(rowDatas[5]);
    }

    public String getRowDatasForFileStorage() {
        String rowData = name + DATA_SEPARATOR;
        rowData += category + DATA_SEPARATOR;
        rowData += energeticValue + DATA_SEPARATOR;
        rowData += getFormattedRate(proteinRate) + DATA_SEPARATOR;
        rowData += getFormattedRate(glucidRate) + DATA_SEPARATOR;
        rowData += getFormattedRate(lipidRate);

        return rowData;
    }

    public String toString() {
        return String.format(FORMAT_TO_STRING,
                name,
                category,
                energeticValue,
                getFormattedRate(proteinRate),
                getFormattedRate(glucidRate),
                getFormattedRate(lipidRate));
    }

    public boolean equals(Food oFood) {
        return (name.equals(oFood.name) &&
                category.equals(oFood.category) &&
                energeticValue == oFood.energeticValue &&
                proteinRate == oFood.proteinRate &&
                glucidRate == oFood.glucidRate &&
                lipidRate == oFood.lipidRate
        );
    }

    public boolean isFulfilled() {
        return (!name.isEmpty() &&
                !category.isEmpty() &&
                (energeticValue != -1) &&
                (proteinRate != -1) &&
                (glucidRate != -1) &&
                (lipidRate != -1)
        );
    }

    private boolean isAttributeEmpty(eFoodAttributes foodAttribute) {
        boolean isEmpty = true;

        switch (foodAttribute) {
            case NAME:
                isEmpty = (name.isEmpty());
                break;
            case CATEGORY:
                isEmpty = (category.isEmpty());
                break;
            case ENERGETIC_VALUE:
                isEmpty = (energeticValue == -1);
                break;
            case LIPID_RATE:
                isEmpty = (lipidRate == -1);
                break;
            case GLUCID_RATE:
                isEmpty = (glucidRate == -1);
                break;
            case PROTEIN_RATE:
                isEmpty = (proteinRate == -1);
        }

        return isEmpty;
    }

    public eFoodAttributes getNextAttributeToFill() {

        eFoodAttributes foodAttribute = null;

        for (eFoodAttributes it : eFoodAttributes.values()) {
            if (isAttributeEmpty(it)) {
                foodAttribute = it;
                break;
            }
        }

        return foodAttribute;

    }

    private void setName(String name) throws DataFormatException {
        if (name.isEmpty()) {
            throw new DataFormatException("Food name is mandatory !");
        } else {
            this.name = name;
        }
    }

    private void setCategory(String category) throws DataFormatException {
        if (category.isEmpty()) {
            throw new DataFormatException("Food category is mandatory !");
        } else {
            this.category = category;
        }
    }


    private void setEnergeticValue(String energeticValue) throws DataFormatException {
        boolean isInputOK ;
        int testedValue = -1;
        try {
            testedValue = Integer.parseInt(energeticValue);
            isInputOK = (testedValue > 0);
        } catch (NumberFormatException n) {
            isInputOK = false;
        }
        if (isInputOK) {
            this.energeticValue = testedValue;
        } else {
            throw new DataFormatException("Energetic value is mandatory and has to be greater than 0 !");
        }

    }

    private void setAttributeRateValue(eFoodAttributes eFoodAttribute, String rateValue) throws DataFormatException{
        boolean isInputOK;
        int testedValue=-1;
        try {
            testedValue= setRate(rateValue);
            isInputOK = (testedValue>0 && testedValue<=10000);

        } catch (NumberFormatException n){
            isInputOK=false;
        }
        if (isInputOK){
            switch(eFoodAttribute){
                case GLUCID_RATE:
                    this.glucidRate=testedValue;
                    break;
                case PROTEIN_RATE:
                    this.proteinRate=testedValue;
                    break;
                case LIPID_RATE:
                    this.lipidRate=testedValue;
                    break;
            }
        } else{
            throw new DataFormatException(eFoodAttribute.toString()+ " is mandatory and has to be greater than 0 and has to be lower than 100 !");
        }


    }

    public void setAttribute(eFoodAttributes eFoodAttribute, String userValue) throws DataFormatException {

        switch (eFoodAttribute) {
            case NAME:
                setName(userValue);
                break;
            case CATEGORY:
                setCategory(userValue);
                break;
            case ENERGETIC_VALUE:
                setEnergeticValue(userValue);
                break;
            case GLUCID_RATE:
            case LIPID_RATE:
            case PROTEIN_RATE:
                setAttributeRateValue(eFoodAttribute, userValue);
                break;

        }

    }

}
