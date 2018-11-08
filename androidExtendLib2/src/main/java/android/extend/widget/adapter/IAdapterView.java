package android.extend.widget.adapter;

import android.view.ActionMode;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

public interface IAdapterView<T extends BaseAdapter<?>>
{
	/**
	 * Interface definition for a callback to be invoked when an item in this
	 * AdapterView has been clicked.
	 */
	public interface OnItemClickListener
	{
		/**
		 * Callback method to be invoked when an item in this AdapterView has
		 * been clicked.
		 * <p>
		 * Implementers can call getItemAtPosition(position) if they need to access the data associated with the
		 * selected item.
		 * 
		 * @param parent The AdapterView where the click happened.
		 * @param view The view within the AdapterView that was clicked (this
		 *            will be a view provided by the adapter)
		 * @param position The position of the view in the adapter.
		 * @param id The row id of the item that was clicked.
		 */
		void onItemClick(View adapterView, ViewGroup parent, View view, int position, long id);
	}

	/**
	 * Interface definition for a callback to be invoked when an item in this
	 * view has been clicked and held.
	 */
	public interface OnItemLongClickListener
	{
		/**
		 * Callback method to be invoked when an item in this view has been
		 * clicked and held.
		 * 
		 * Implementers can call getItemAtPosition(position) if they need to access
		 * the data associated with the selected item.
		 * 
		 * @param parent The AbsListView where the click happened
		 * @param view The view within the AbsListView that was clicked
		 * @param position The position of the view in the list
		 * @param id The row id of the item that was clicked
		 * 
		 * @return true if the callback consumed the long click, false otherwise
		 */
		boolean onItemLongClick(View adapterView, ViewGroup parent, View view, int position, long id);
	}

	/**
	 * Interface definition for a callback to be invoked when
	 * an item in this view has been selected.
	 */
	public interface OnItemSelectedListener
	{
		/**
		 * <p>
		 * Callback method to be invoked when an item in this view has been selected. This callback is invoked only when
		 * the newly selected position is different from the previously selected position or if there was no selected
		 * item.
		 * </p>
		 * 
		 * Impelmenters can call getItemAtPosition(position) if they need to access the
		 * data associated with the selected item.
		 * 
		 * @param parent The AdapterView where the selection happened
		 * @param view The view within the AdapterView that was clicked
		 * @param position The position of the view in the adapter
		 * @param id The row id of the item that is selected
		 */
		void onItemSelected(View adapterView, ViewGroup parent, View view, int position, long id);

		void onItemUnSelected(View adapterView, ViewGroup parent, View view, int position, long id);

		/**
		 * Callback method to be invoked when the selection disappears from this
		 * view. The selection can disappear for instance when touch is activated
		 * or when the adapter becomes empty.
		 * 
		 * @param parent The AdapterView that now contains no selected item.
		 */
		void onNothingSelected(View adapterView);
	}

	/**
	 * A MultiChoiceModeListener receives events for {@link AbsListView#CHOICE_MODE_MULTIPLE_MODAL}.
	 * It acts as the {@link ActionMode.Callback} for the selection mode and also receives
	 * {@link #onItemCheckedStateChanged(ActionMode, int, long, boolean)} events when the user
	 * selects and deselects list items.
	 */
	public interface OnMultiChoiceModeListener
	{
		/**
		 * Called when an item is checked or unchecked during selection mode.
		 * 
		 * @param mode The {@link ActionMode} providing the selection mode
		 * @param position Adapter position of the item that was checked or unchecked
		 * @param id Adapter ID of the item that was checked or unchecked
		 * @param checked <code>true</code> if the item is now checked, <code>false</code> if the item is now unchecked.
		 */
		public void onItemCheckedStateChanged(int position, long id, boolean checked);
	}

	/**
	 * Handler Message Init Layout
	 * */
	public static final int MSG_INIT_LAYOUT = 0x00;
	/**
	 * Handler Message Init Content
	 * */
	public static final int MSG_INIT_CONTENT = 0x01;
	/**
	 * Handler Message Add Content
	 * */
	public static final int MSG_ADD_CONTENT = 0x02;
	/**
	 * Handler Message Remove Content
	 * */
	public static final int MSG_REMOVE_CONTENT = 0x03;
	/**
	 * Handler Message Update Content
	 * */
	public static final int MSG_UPDATE_CONTENT = 0x04;
	/**
	 * Handler Message Compute Visible Content
	 * */
	public static final int MSG_COMPUTE_VISIBLECONTENT = 0x05;
	/**
	 * Handler Message Change Vertical Divider
	 * */
	public static final int MSG_CHANGE_VERTICALDIVIDER = 0x06;
	/**
	 * Handler Message Change Horizontal Divider
	 * */
	public static final int MSG_CHANGE_HORIZONTALDIVIDER = 0x07;
	/**
	 * Handler Message Change Selector
	 * */
	public static final int MSG_CHANGE_SELECTOR = 0x08;
	/**
	 * Handler Message Change Selection
	 * */
	public static final int MSG_CHANGE_SELECTION = 0x09;

	/**
	 * Normal list that does not indicate choices
	 */
	public static final int CHOICE_MODE_NONE = 0;

	/**
	 * The list allows up to one choice
	 */
	public static final int CHOICE_MODE_SINGLE = 1;

	/**
	 * The list allows multiple choices
	 */
	public static final int CHOICE_MODE_MULTIPLE = 2;

	public void setAdapter(T adapter);

	public T getAdapter();

	public void setVerticalDividerWidth(int width);

	public void setVerticalDividerResource(int resId);

	public void setVerticalDividerColor(int color);

	public void setHorizontalDividerHeight(int height);

	public void setHorizontalDividerResource(int resId);

	public void setHorizontalDividerColor(int color);

	public void setSelector(int resId);

	public void setSelectorPadding(int padding);

	public void setSelectable(boolean selectable);

	public void setSelection(int position);

	public View getSelectedView();

	public int getSelectedPosition();

	public View getItemView(int position);

	public void setOnItemClickListener(OnItemClickListener listener);

	public void setOnItemLongClickListener(OnItemLongClickListener listener);

	public void setOnItemSelectedListener(OnItemSelectedListener listener);
}
