/*
 * Tencent is pleased to support the open source community by making Tencent Shadow available.
 * Copyright (C) 2019 THL A29 Limited, a Tencent company.  All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use
 * this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 *     https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package com.tencent.shadow.sample.plugin.app.lib.usecases.host_communication;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AlertDialog;

import com.tencent.shadow.sample.host.lib.HostUiLayerProvider;
import com.tencent.shadow.sample.host.lib.MPermissions;
import com.tencent.shadow.sample.plugin.app.lib.R;
import com.tencent.shadow.sample.plugin.app.lib.gallery.BaseActivity;
import com.tencent.shadow.sample.plugin.app.lib.gallery.cases.entity.UseCase;

public class PluginUseHostClassActivity extends BaseActivity {
    public static class Case extends UseCase {
        @Override
        public String getName() {
            return "插件使用宿主类测试";
        }

        @Override
        public String getSummary() {
            return "测试插件中调用宿主类的方法";
        }

        @Override
        public Class getPageClass() {
            return PluginUseHostClassActivity.class;
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.TestHostTheme);

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        HostUiLayerProvider hostUiLayerProvider = HostUiLayerProvider.getInstance();
        View hostUiLayer = hostUiLayerProvider.buildHostUiLayer();
        linearLayout.addView(hostUiLayer);

        setContentView(linearLayout);


        Button textView = new Button(this);
        textView.setText("模拟插件向宿主调用请求权限功能，弹出AppCompat会崩溃");
        linearLayout.addView(textView);
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MPermissions.getInstance().requestPermission(PluginUseHostClassActivity.this);
            }
        });

        Button testDialog = new Button(this);
        testDialog.setText("插件自己弹AppCompatDialog没事");
        testDialog.setOnClickListener(v -> {
            new AlertDialog.Builder(PluginUseHostClassActivity.this)
                    .setTitle("权限 Title")
                    .setMessage("权限 Msg")
                    .setOnDismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            dialog.dismiss();
                        }
                    })
                    .setPositiveButton("go", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();

                        }
                    })
                    .show();
        });
        linearLayout.addView(testDialog);
    }
}
