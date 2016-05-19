import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import weka.classifiers.Classifier;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;

public class BaggedClassifier<T extends Classifier> extends Classifier {

	/** for serialization */
	static final long serialVersionUID = -2693678647096322562L;
	
	static final boolean useRandomSeed = false;

	private List<T> m_forrest;
	private Class<T> m_classFactory;
	private int m_samplingCount;

	public BaggedClassifier(int samplingCount, Class<T> cls) {
		m_classFactory = cls;
		m_samplingCount = samplingCount;
		
		m_forrest = new ArrayList();
	}

	public Capabilities getCapabilities() {
		Capabilities result = super.getCapabilities();
		result.disableAll();

		// attributes
		result.enable(Capability.NOMINAL_ATTRIBUTES);

		result.enable(Capability.MISSING_VALUES);

		// class
		result.enable(Capability.NOMINAL_CLASS);
		
		// instances
		result.setMinimumNumberInstances(0);

		return result;
	}

	public void buildClassifier(Instances data) throws Exception {
		// can classifier handle the data?
		getCapabilities().testWithFail(data);

		// remove instances with missing class
		data = new Instances(data);
		data.deleteWithMissingClass();

		Random rnd = new Random(System.currentTimeMillis());
		for (int i = 0; i < m_samplingCount; i++) {
			Instances newDataSet = data;
			
			if (i != 0) {
				newDataSet = new Instances(data, data.numInstances());
				
				long seed = useRandomSeed ? rnd.nextLong() : i;
				ShuffleData(data, newDataSet, seed);
			}
			
			T c = m_classFactory.newInstance();
			c.buildClassifier(newDataSet);
			
			m_forrest.add(c);
		}
	}

	public double classifyInstance(Instance instance) throws Exception {
		double[] classes = new double[m_samplingCount];
		double sum = 0.0;
		for (int i = 0; i < m_samplingCount; i++) {
			classes[i] = m_forrest.get(i).classifyInstance(instance);
			sum += classes[i];
		}

		return (double)Math.round(sum/(double)m_samplingCount);
	}

	public String convertClassValueToString(Attribute classAttribute, double classValue) {
		return classAttribute.value((int) classValue);
	}
	
	private void ShuffleData(Instances data, Instances newData, long seed) {
		Random rnd = new Random(seed);
		int size = data.numInstances();
		for (int i = 0; i < size; i++) {
			int idx = rnd.nextInt(size);
			newData.add(data.instance(idx));
		}
	}
}
