package be.digitalia.common.adapters;

import android.database.DataSetObserver;
import android.util.SparseIntArray;
import android.widget.Adapter;
import android.widget.SectionIndexer;

/**
 * An optimized version of the framework's AlphabetIndexer which is compatible with any Adapter
 * returning non-null items with a meaningful toString() representation. Section indexes will be
 * updated automatically when the Adapter's data set changes.
 * 
 * @author Christophe Beyls
 * 
 */
public class GenericAlphabetIndexer extends DataSetObserver implements SectionIndexer {
	/**
	 * BaseAdapter that is used as data source.
	 */
	protected Adapter mAdapter;
	private int mAdapterCount = -1;

	/**
	 * The string of characters that make up the indexing sections.
	 */
	protected CharSequence mAlphabet;

	/**
	 * Cached length of the alphabet array.
	 */
	private int mAlphabetLength;

	/**
	 * This contains a cache of the computed indices so far. It will get reset whenever the dataset
	 * changes or the cursor changes.
	 */
	private SparseIntArray mAlphaMap;

	/**
	 * Use a collator to compare strings in a localized manner.
	 */
	private java.text.Collator mCollator;

	/**
	 * The section array converted from the alphabet string.
	 */
	private String[] mAlphabetArray;

	/**
	 * Constructs the indexer.
	 * 
	 * @param adapter
	 *            the adapter providing the data set. The index will be built by calling toString()
	 *            on the objects returned by getItem(). The objects must be sorted alphabetically.
	 * @param alphabet
	 *            string containing the alphabet, with space as the first character. For example,
	 *            use the string " ABCDEFGHIJKLMNOPQRSTUVWXYZ" for English indexing. The characters
	 *            must be uppercase and be sorted in ascii/unicode order. Basically characters in
	 *            the alphabet will show up as preview letters.
	 */
	public GenericAlphabetIndexer(Adapter adapter, String alphabet) {
		if (adapter == null) {
			throw new IllegalArgumentException("adapter must not be null");
		}
		mAdapter = adapter;
		mAlphabet = alphabet;
		mAlphabetLength = alphabet.length();
		mAlphabetArray = new String[mAlphabetLength];
		for (int i = 0; i < mAlphabetLength; i++) {
			mAlphabetArray[i] = Character.toString(mAlphabet.charAt(i));
		}
		mAlphaMap = new SparseIntArray(mAlphabetLength);
		mAdapter.registerDataSetObserver(this);
		// Get a Collator for the current locale for string comparisons.
		mCollator = java.text.Collator.getInstance();
		mCollator.setStrength(java.text.Collator.PRIMARY);
	}

	/**
	 * Returns the section array constructed from the alphabet provided in the constructor.
	 * 
	 * @return the section array
	 */
	@Override
	public Object[] getSections() {
		return mAlphabetArray;
	}

	/**
	 * Default implementation compares the first character of word with letter.
	 */
	protected int compare(String word, String letter) {
		final String firstLetter;
		if (word.length() == 0) {
			firstLetter = " ";
		} else {
			firstLetter = word.substring(0, 1);
		}

		return mCollator.compare(firstLetter, letter);
	}

	/**
	 * Performs a binary search or cache lookup to find the first row that matches a given section's
	 * starting letter.
	 * 
	 * @param sectionIndex
	 *            the section to search for
	 * @return the row index of the first occurrence, or the nearest next letter. For instance, if
	 *         searching for "T" and no "T" is found, then the first row starting with "U" or any
	 *         higher letter is returned. If there is no data following "T" at all, then the list
	 *         size is returned.
	 */
	@Override
	public int getPositionForSection(int sectionIndex) {
		final SparseIntArray alphaMap = mAlphaMap;
		final Adapter adapter = mAdapter;

		if (mAlphabet == null) {
			return 0;
		}

		// Check bounds
		if (sectionIndex <= 0) {
			return 0;
		}
		if (sectionIndex >= mAlphabetLength) {
			sectionIndex = mAlphabetLength - 1;
		}

		int key = mAlphabet.charAt(sectionIndex);
		int pos;
		// Check map
		if (Integer.MIN_VALUE != (pos = alphaMap.get(key, Integer.MIN_VALUE))) {
			return pos;
		}

		String targetLetter = mAlphabetArray[sectionIndex];
		if (mAdapterCount == -1) {
			mAdapterCount = adapter.getCount();
		}
		int count = mAdapterCount;
		int start = 0;
		int end = count;

		// Do we have the position of the previous section?
		if (sectionIndex > 0) {
			int prevLetter = mAlphabet.charAt(sectionIndex - 1);
			int prevLetterPos = alphaMap.get(prevLetter, Integer.MIN_VALUE);
			if (prevLetterPos != Integer.MIN_VALUE) {
				start = prevLetterPos;
			}
		}

		// Now that we have a possibly optimized start and end, let's binary search

		pos = (end + start) / 2;

		while (pos < end) {
			// Get letter at pos
			String curName = adapter.getItem(pos).toString();
			int diff = compare(curName, targetLetter);
			if (diff != 0) {
				if (diff < 0) {
					start = pos + 1;
					if (start >= count) {
						pos = count;
						break;
					}
				} else {
					end = pos;
				}
			} else {
				// They're the same, but that doesn't mean it's the start
				if (start == pos) {
					// This is it
					break;
				} else {
					// Need to go further lower to find the starting row
					end = pos;
				}
			}
			pos = (start + end) / 2;
		}
		alphaMap.put(key, pos);
		return pos;
	}

	/**
	 * Returns the section index for a given position in the list by querying the item and comparing
	 * it with all items in the section array.
	 */
	@Override
	public int getSectionForPosition(int position) {
		// Bounds check
		if (position < 0) {
			return 0;
		} else {
			if (mAdapterCount == -1) {
				mAdapterCount = mAdapter.getCount();
			}
			if (position >= mAdapterCount) {
				position = mAdapterCount - 1;
			}
		}

		String curName = mAdapter.getItem(position).toString();
		// Linear search, as there are only a few items in the section index
		// Could speed this up later if it actually gets used.
		for (int i = 0; i < mAlphabetLength; i++) {
			String targetLetter = mAlphabetArray[i];
			if (compare(curName, targetLetter) == 0) {
				return i;
			}
		}
		return 0; // Don't recognize the letter - falls under zero'th section
	}

	@Override
	public void onChanged() {
		super.onChanged();
		mAlphaMap.clear();
		mAdapterCount = -1;
	}

	@Override
	public void onInvalidated() {
		super.onInvalidated();
		mAlphaMap.clear();
		mAdapterCount = -1;
	}
}