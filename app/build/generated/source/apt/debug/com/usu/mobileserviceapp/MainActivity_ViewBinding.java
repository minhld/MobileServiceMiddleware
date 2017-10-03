// Generated code from Butter Knife. Do not modify!
package com.usu.mobileserviceapp;

import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import butterknife.Unbinder;
import butterknife.internal.Utils;
import java.lang.IllegalStateException;
import java.lang.Override;

public class MainActivity_ViewBinding<T extends MainActivity> implements Unbinder {
  protected T target;

  public MainActivity_ViewBinding(T target, View source) {
    this.target = target;

    target.createGroupBtn = Utils.findRequiredViewAsType(source, R.id.createGroupBtn, "field 'createGroupBtn'", Button.class);
    target.discoverBtn = Utils.findRequiredViewAsType(source, R.id.discoverBtn, "field 'discoverBtn'", Button.class);
    target.pubBtn = Utils.findRequiredViewAsType(source, R.id.pubBtn, "field 'pubBtn'", Button.class);
    target.subBtn = Utils.findRequiredViewAsType(source, R.id.subBtn, "field 'subBtn'", Button.class);
    target.workerBtn = Utils.findRequiredViewAsType(source, R.id.workerBtn, "field 'workerBtn'", Button.class);
    target.deviceList = Utils.findRequiredViewAsType(source, R.id.deviceList, "field 'deviceList'", ListView.class);
    target.infoText = Utils.findRequiredViewAsType(source, R.id.infoText, "field 'infoText'", TextView.class);
    target.viewer = Utils.findRequiredViewAsType(source, R.id.imageView, "field 'viewer'", ImageView.class);
  }

  @Override
  public void unbind() {
    T target = this.target;
    if (target == null) throw new IllegalStateException("Bindings already cleared.");

    target.createGroupBtn = null;
    target.discoverBtn = null;
    target.pubBtn = null;
    target.subBtn = null;
    target.workerBtn = null;
    target.deviceList = null;
    target.infoText = null;
    target.viewer = null;

    this.target = null;
  }
}
