package com.xpirator.db;

import java.sql.SQLException;

import com.j256.ormlite.android.AndroidCompiledStatement;
import com.j256.ormlite.android.AndroidDatabaseResults;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.StatementBuilder;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.ResourceCursorTreeAdapter;

/*
 * @author Richard
 * 
 * G is group's data type
 * C is child's data type in a specific group
 */
public class TreeQueryAdapter<G, C> extends ResourceCursorTreeAdapter {
  
  protected static OrmLiteSqliteOpenHelper dbh = DatabaseManager.getInstance().getHelper();
  
  protected PreparedQuery<G> mGroupQuery;
  protected PreparedQuery<C> mChildQuery;
  
  protected GroupItemBinder<G> mGroupBinder;
  protected ChildItemBinder<C> mChildBinder;
  
  protected ChildQueryBinder<C> mChildQueryBinder;

  public TreeQueryAdapter(Activity caller, 
                          PreparedQuery<G> gq, 
                          ChildQueryBinder<C> cqb, 
                          GroupItemBinder<G> gb, 
                          ChildItemBinder<C> cb,
                          int groupLayout, 
                          int childLayout ) throws SQLException {
    super (
      caller, 
      ((AndroidCompiledStatement) gq.compile(dbh.getConnectionSource().getReadOnlyConnection(),
      StatementBuilder.StatementType.SELECT)).getCursor(), 
      groupLayout, 
      childLayout );

    mGroupQuery = gq;
    mChildQueryBinder = cqb;
    
    mGroupBinder = gb;
    mChildBinder = cb;

    // Don't need to call startManagingCursor(). ORMLite takes all responsibility out for managing the given Cursor.  
    // caller.startManagingCursor();
  }

  @Override
  protected void bindChildView(View item, Context context, Cursor cursor, boolean isLastChild) {
    try {
      C c = mChildQuery.mapRow(new AndroidDatabaseResults(cursor, null));
      mChildBinder.setItemContent(item, c, cursor);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }

  @Override
  protected void bindGroupView(View item, Context context, Cursor cursor, boolean isExpanded) {
    try {
      G g = mGroupQuery.mapRow(new AndroidDatabaseResults(cursor, null));
      mGroupBinder.setItemContent(item, g, cursor);
    } catch (SQLException e) {
      e.printStackTrace();
    }
  }
  
  public static interface GroupItemBinder<G> {
    void setItemContent(View groupItem, G g, Cursor cursor);
  }
  
  public static interface ChildItemBinder<C> {
    void setItemContent(View listItem, C c, Cursor cursor);
  }
  
  public static interface ChildQueryBinder<C> {
    PreparedQuery<C> getChildPreparedQuery(Cursor groupCursor) throws SQLException;
  }

  @Override
  protected Cursor getChildrenCursor(Cursor groupCursor) {
    if(mChildQueryBinder == null) return null;
    
    Cursor cursor = null;
    try {
      mChildQuery = mChildQueryBinder.getChildPreparedQuery(groupCursor);
      cursor = ((AndroidCompiledStatement) 
        mChildQuery.compile( dbh.getConnectionSource().getReadOnlyConnection(),
        StatementBuilder.StatementType.SELECT)).getCursor();
    } catch (SQLException e) {
      e.printStackTrace();
    }
    return cursor;
  }
    
  @Override
  public boolean isChildSelectable(int groupPosition, int childPosition) {
    return true;
  }
}