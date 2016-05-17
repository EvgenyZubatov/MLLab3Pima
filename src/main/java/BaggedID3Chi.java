/*
 *    This program is free software; you can redistribute it and/or modify
 *    it under the terms of the GNU General Public License as published by
 *    the Free Software Foundation; either version 2 of the License, or
 *    (at your option) any later version.
 *
 *    This program is distributed in the hope that it will be useful,
 *    but WITHOUT ANY WARRANTY; without even the implied warranty of
 *    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *    GNU General Public License for more details.
 *
 *    You should have received a copy of the GNU General Public License
 *    along with this program; if not, write to the Free Software
 *    Foundation, Inc., 675 Mass Ave, Cambridge, MA 02139, USA.
 */

/*
 * Copied original implementation of Id3 by 
 * @author Eibe Frank (eibe@cs.waikato.ac.nz)
 * @version $Revision: 6404 $ 
*/

//package weka.classifiers.trees;

import java.util.Enumeration;

import weka.classifiers.Classifier;
import weka.classifiers.Sourcable;
import weka.core.Attribute;
import weka.core.Capabilities;
import weka.core.Capabilities.Capability;
import weka.core.Instance;
import weka.core.Instances;
import weka.core.NoSupportForMissingValuesException;
import weka.core.RevisionUtils;
import weka.core.TechnicalInformation;
import weka.core.TechnicalInformation.Field;
import weka.core.TechnicalInformation.Type;
import weka.core.TechnicalInformationHandler;
import weka.core.Utils;

import org.apache.commons.math3.distribution.ChiSquaredDistribution;

/**
 * <!-- globalinfo-start --> Class for constructing an unpruned decision tree
 * based on the ID3 algorithm. Can only deal with nominal attributes. No missing
 * values allowed. Empty leaves may result in unclassified instances. For more
 * information see: <br/>
 * <br/>
 * R. Quinlan (1986). Induction of decision trees. Machine Learning.
 * 1(1):81-106.
 * <p/>
 * <!-- globalinfo-end -->
 *
 * <!-- technical-bibtex-start --> BibTeX:
 * 
 * <pre>
 * &#64;article{Quinlan1986,
 *    author = {R. Quinlan},
 *    journal = {Machine Learning},
 *    number = {1},
 *    pages = {81-106},
 *    title = {Induction of decision trees},
 *    volume = {1},
 *    year = {1986}
 * }
 * </pre>
 * <p/>
 * <!-- technical-bibtex-end -->
 *
 * <!-- options-start --> Valid options are:
 * <p/>
 * 
 * <pre>
 *  -D
 *  If set, classifier is run in debug mode and
 *  may output additional info to the console
 * </pre>
 * 
 * <!-- options-end -->
 *
 */
public class BaggedID3Chi extends Classifier {

	/** for serialization */
	static final long serialVersionUID = -2693678647096322562L;

	/** The node's successors. */
	private ID3Chi[] m_forrest;

	/** Confidence level 0.95 for 95%, 0.99 for 99%, etc */
	private double m_confidenceLevel;

	static final int MinNumberOfExpectedValuesForChi = 5;
	static final double MaxPercentageOfSmallCategoriesForChi = 0.2;
	
	public BaggedID3Chi(double confidenceLevel) {
		m_confidenceLevel = confidenceLevel;	
	}

	/**
	 * Returns default capabilities of the classifier.
	 *
	 * @return the capabilities of this classifier
	 */
	public Capabilities getCapabilities() {
		Capabilities result = super.getCapabilities();
		result.disableAll();

		// attributes
		result.enable(Capability.NOMINAL_ATTRIBUTES);

		// Use proportional distribution and token classification approach from
		// ID3 paper
		result.enable(Capability.MISSING_VALUES);

		// class
		result.enable(Capability.NOMINAL_CLASS);
		
		// don't allow missing class values
		//result.enable(Capability.MISSING_CLASS_VALUES);

		// instances
		result.setMinimumNumberInstances(0);

		return result;
	}

	/**
	 * Builds ID3Chi decision tree classifier.
	 *
	 * @param data
	 *            the training data
	 * @exception Exception
	 *                if classifier can't be built successfully
	 */
	public void buildClassifier(Instances data) throws Exception {

		// can classifier handle the data?
		getCapabilities().testWithFail(data);

		// remove instances with missing class
		data = new Instances(data);
		data.deleteWithMissingClass();

		//makeTree(data);
	}

	/**
	 * Classifies a given test instance using the decision tree.
	 *
	 * @param instance
	 *            the instance to be classified
	 * @return the classification
	 * @throws NoSupportForMissingValuesException
	 *             if instance has missing values
	 */
	public double classifyInstance(Instance instance) {

		//double [] tokenDistribution = classifyInstanceWithToken(instance, 1.0);
		return 0;//Utils.maxIndex(tokenDistribution);
	}

	public String convertClassValueToString(Attribute classAttribute, double classValue) {
		return classAttribute.value((int) classValue);
	}
}
