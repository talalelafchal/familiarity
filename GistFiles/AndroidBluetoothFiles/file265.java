package mykodetutorial.blogspot.com.easycustomtogglebutton;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import eu.chainfire.libsuperuser.Shell;

/**
 * Created by Suyono on 5/10/2016.
 * Copyright (c) 2016 by Suyono (ion).
 * All rights reserved.
 * This product is protected by copyright and distributed under
 * licenses restricting copying, distribution and decompilation.
 */
public class ToggleView extends LinearLayout {
    /************************************
    * Mendeklarasikan semua variabel
    *************************************/
    Context mContext;
    //ToggleButton
    ToggleButton toggle_power;
    ToggleButton toggle_reboot;
    ToggleButton toggle_soft_reboot;
    ToggleButton toggle_safe_mode;
    ToggleButton toggle_revovery;
    ToggleButton toggle_bootloader;
    ToggleButton toggle_shutdown;
    ToggleButton toggle_restartui;
    ToggleButton toggle_data;
    ToggleButton toggle_wifi;
    ToggleButton toggle_bluetooth;
    ToggleButton toggle_pesawat;

    /* variabel script Shell su */
    private static final String[] REBOOT_SAFE_MODE
            = new String[]{"setprop persist.sys.safemode 1", "setprop ctl.restart zygote"};
    private static final String[] MODE_PESAWAT_NYALA
            = new String[] {"settings put global airplane_mode_on 1","am broadcast -a android.intent.action.AIRPLANE_MODE --ez state true"};
    private static final String[] MODE_PESAWAT_MATI
            = new String[] {"settings put global airplane_mode_on 0","am broadcast -a android.intent.action.AIRPLANE_MODE --ez state false"};
    
    /* String dialog */
    String BELUM_ROOT =  "Phone not Rooted..!! Sebaiknya di Root terlebih dahulu Androidnya ya gan :)";
    String[] MUNCUL_DIALOG_TEXT = {
            //MUNCUL_DIALOG_TEXT[0]
            "Tindakan ini akan memulai kembali device anda. Apakah anda yakin ?",
            //MUNCUL_DIALOG_TEXT[1]
            "Tindakan ini akan memulai kembali device anda secara soft. Apakah anda yakin ?",
            //MUNCUL_DIALOG_TEXT[2]
            "Tindakan ini akan menyalakan device anda dalam mode aman (Safe Mode). Apakah anda yakin ?",
            //MUNCUL_DIALOG_TEXT[3]
            "Tindakan ini akan masuk kedalam mode Recovery (Stock/Custom Recovey). Apakah anda yakin ?",
            //MUNCUL_DIALOG_TEXT[4]
            "Tindakan ini akan menuju ke mode Bootloader / fastboot. Apakah anda yakin ?",
            //MUNCUL_DIALOG_TEXT[5]
            "Tindakan ini akan mematikan device anda. Apakah anda yakin ?",
            //MUNCUL_DIALOG_TEXT[6]
            "Tindakan ini akan memulai kembali SystemUI. Apakah anda yakin ?"
    };
    String BELUM_ROOT_KOK_UDAH_NYOBAIN_SIH = "Diperlukan akses root, silahkan di root terlebih dahulu gan...!! Terimakasih.";

    // SharedPreferences
    SharedPreferences pref_power;
    SharedPreferences pref_data;
    SharedPreferences pref_wifi;
    SharedPreferences pref_bluetooth;
    SharedPreferences pref_pesawat;

    // Custom Dialog
    Dialog dialog_view;
    ImageView dialog_icon;
    TextView dialog_text, dialog_mykodetutorial;
    Button dialog_tombol_ok, dialog_tombol_cancel;

    /************************************
     * Constructor parameters
     * *********************************
     */
    public ToggleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        if (!isInEditMode()) {
            mContext = context;
            myKode(); //menerapkan kode mykode() disini
        }
    }

    public ToggleView(Context context) {
        super(context,null);
        if (!isInEditMode()) {
            mContext = context;
            myKode(); //menerapkan kode mykode() disini
        }
    }

    /************************************
     * kode Utama myKode()
     */
    private void myKode() {
        /* inflate layout toggle_view.xml
        * menempatkan target view dengan mengambil berdasarkan id
        * */
        LayoutInflater inflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View root = inflater.inflate(setResource("toggle_view","layout"), this, true);

        /* Target id
        *  yang ada didalam root(toggle_view.xml)
        * */
        toggle_power = (ToggleButton) root.findViewById(setResource("toggle_power","id"));
        toggle_reboot = (ToggleButton) root.findViewById(setResource("toggle_reboot","id"));
        toggle_soft_reboot = (ToggleButton) root.findViewById(setResource("toggle_soft_reboot","id"));
        toggle_safe_mode = (ToggleButton) root.findViewById(setResource("toggle_safe_mode","id"));
        toggle_revovery = (ToggleButton) root.findViewById(setResource("toggle_revovery","id"));
        toggle_bootloader = (ToggleButton) root.findViewById(setResource("toggle_bootloader","id"));
        toggle_shutdown = (ToggleButton) root.findViewById(setResource("toggle_shutdown","id"));
        toggle_restartui = (ToggleButton) root.findViewById(setResource("toggle_restartui","id"));
        toggle_data = (ToggleButton) root.findViewById(setResource("toggle_mobile_data","id"));
        toggle_wifi = (ToggleButton) root.findViewById(setResource("toggle_wifi","id"));
        toggle_bluetooth = (ToggleButton) root.findViewById(setResource("toggle_bluetooth","id"));
        toggle_pesawat = (ToggleButton) root.findViewById(setResource("toggle_pesawat","id"));

        /* Custom dialog view */
        dialog_view = new Dialog(mContext);
        dialog_view.setContentView(setResource("dialog_view","layout"));
        dialog_icon = (ImageView) dialog_view.findViewById(setResource("dialog_taruh_icon","id"));
        dialog_text = (TextView) dialog_view.findViewById(setResource("dialog_taruh_text","id"));
        dialog_tombol_ok = (Button) dialog_view.findViewById(setResource("dialog_tombol_ok","id"));
        dialog_tombol_cancel = (Button) dialog_view.findViewById(setResource("dialog_tombol_batal","id"));
        dialog_mykodetutorial = (TextView) dialog_view.findViewById(setResource("dialog_mykode_tutorial","id"));
        dialog_mykodetutorial.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                pergiKeURL("https://mykodetutorial.blogspot.com");
            }
        });

        /* Cek Root saat pertama kali memasang guide */
        if (!Shell.SU.available()){
            LinearLayout cekrootView = (LinearLayout) root.findViewById(setResource("cekroot_view","id"));
            ImageView cekrootIcon = (ImageView) root.findViewById(setResource("cekroot_icon","id"));
            TextView cekrootPesan = (TextView) root.findViewById(setResource("cekroot_pesan","id"));
            cekrootView.setVisibility(View.VISIBLE);
            cekrootIcon.setImageResource(setResource("su_icon","drawable"));
            cekrootPesan.setText(BELUM_ROOT_KOK_UDAH_NYOBAIN_SIH);
        }
        /*************************************
         * Tindakan jika ToggleButton di klik
         *************************************/

        /* 1. power menu */
        toggle_power.setOnClickListener(new ToggleButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = pref_power.edit();
                editor.putBoolean("tgpower", toggle_power.isChecked());
                editor.apply();
            }
        });
        toggle_power.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    toggle_reboot.setVisibility(View.VISIBLE);
                    toggle_soft_reboot.setVisibility(View.VISIBLE);
                    toggle_safe_mode.setVisibility(View.VISIBLE);
                    toggle_revovery.setVisibility(View.VISIBLE);
                    toggle_bootloader.setVisibility(View.VISIBLE);
                    toggle_shutdown.setVisibility(View.VISIBLE);
                } else {
                    toggle_reboot.setVisibility(View.GONE);
                    toggle_soft_reboot.setVisibility(View.GONE);
                    toggle_safe_mode.setVisibility(View.GONE);
                    toggle_revovery.setVisibility(View.GONE);
                    toggle_bootloader.setVisibility(View.GONE);
                    toggle_shutdown.setVisibility(View.GONE);
                }
            }
        });
        pref_power = mContext.getSharedPreferences("tgpower", Context.MODE_PRIVATE);
        boolean tgpower = pref_power.getBoolean("tgpower", false);//default false
        toggle_power.setChecked(tgpower);

        /****************************************************************/
        /* 2. reboot */
        toggle_reboot.setOnClickListener(new ToggleButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                // meyakinkan pengguna dengan dialog
                dialog_view.setTitle("Rebooting ...");
                dialog_icon.setImageResource(setResource("reboot_on","drawable"));
                dialog_text.setText(MUNCUL_DIALOG_TEXT[0]);
                dialog_tombol_ok.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Shell.SU.available()) {
                            (new StartUp()).setContext().execute("reboot");
                        } else {
                            munculkanDialogBelumRootUntuk(toggle_reboot);
                        }
                    }
                });
                dialog_tombol_cancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggle_reboot.setChecked(false);
                        dialog_view.dismiss();
                    }
                });
                dialog_view.show();
            }
        });

        /****************************************************************/
        /* 3. soft reboot */
        toggle_soft_reboot.setOnClickListener(new ToggleButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                // meyakinkan pengguna dengan dialog
                dialog_view.setTitle("Soft Rebooting ...");
                dialog_icon.setImageResource(setResource("soft_reboot_on","drawable"));
                dialog_text.setText(MUNCUL_DIALOG_TEXT[1]);
                dialog_tombol_ok.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Shell.SU.available()) {
                            (new StartUp()).setContext().execute("soft_reboot");
                        } else {
                            munculkanDialogBelumRootUntuk(toggle_soft_reboot);
                        }
                    }
                });
                dialog_tombol_cancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggle_soft_reboot.setChecked(false);
                        dialog_view.dismiss();
                    }
                });
                dialog_view.show();
            }
        });

        /****************************************************************/
        /* 4. safe mode */
        toggle_safe_mode.setOnClickListener(new ToggleButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                // meyakinkan pengguna dengan dialog
                dialog_view.setTitle("Safe Mode ...");
                dialog_icon.setImageResource(setResource("safemode_on","drawable"));
                dialog_text.setText(MUNCUL_DIALOG_TEXT[2]);
                dialog_tombol_ok.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Shell.SU.available()) {
                            (new StartUp()).setContext().execute("safe_mode");
                        } else {
                            munculkanDialogBelumRootUntuk(toggle_safe_mode);
                        }
                    }
                });
                dialog_tombol_cancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggle_safe_mode.setChecked(false);
                        dialog_view.dismiss();
                    }
                });
                dialog_view.show();
            }
        });

        /****************************************************************/
        /* 5. recovery */
        toggle_revovery.setOnClickListener(new ToggleButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                // meyakinkan pengguna dengan dialog
                dialog_view.setTitle("Recovery ...");
                dialog_icon.setImageResource(setResource("recovery_on","drawable"));
                dialog_text.setText(MUNCUL_DIALOG_TEXT[3]);
                dialog_tombol_ok.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Shell.SU.available()) {
                            (new StartUp()).setContext().execute("recovery");
                        } else {
                            munculkanDialogBelumRootUntuk(toggle_revovery);
                        }
                    }
                });
                dialog_tombol_cancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggle_revovery.setChecked(false);
                        dialog_view.dismiss();
                    }
                });
                dialog_view.show();
            }
        });
        /****************************************************************/
        /* 6. bootloader */
        toggle_bootloader.setOnClickListener(new ToggleButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                // meyakinkan pengguna dengan dialog
                dialog_view.setTitle("Bootloader / Fastboot ...");
                dialog_icon.setImageResource(setResource("bootloader_on","drawable"));
                dialog_text.setText(MUNCUL_DIALOG_TEXT[4]);
                dialog_tombol_ok.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Shell.SU.available()) {
                            (new StartUp()).setContext().execute("bootloader");
                        } else {
                            munculkanDialogBelumRootUntuk(toggle_bootloader);
                        }
                    }
                });
                dialog_tombol_cancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggle_bootloader.setChecked(false);
                        dialog_view.dismiss();
                    }
                });
                dialog_view.show();
            }
        });

        /****************************************************************/
        /* 7. shutdown */
        toggle_shutdown.setOnClickListener(new ToggleButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                // meyakinkan pengguna dengan dialog
                dialog_view.setTitle("Mematikan ...");
                dialog_icon.setImageResource(setResource("shutdown_on","drawable"));
                dialog_text.setText(MUNCUL_DIALOG_TEXT[5]);
                dialog_tombol_ok.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Shell.SU.available()) {
                            (new StartUp()).setContext().execute("modar");
                        } else {
                            munculkanDialogBelumRootUntuk(toggle_shutdown);
                        }
                    }
                });
                dialog_tombol_cancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggle_shutdown.setChecked(false);
                        dialog_view.dismiss();
                    }
                });
                dialog_view.show();
            }
        });
        /****************************************************************/
        /* 8. reastartui */
        toggle_restartui.setOnClickListener(new ToggleButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                // meyakinkan pengguna dengan dialog
                dialog_view.setTitle("Restarting UI ...");
                dialog_icon.setImageResource(setResource("restart_on","drawable"));
                dialog_text.setText(MUNCUL_DIALOG_TEXT[6]);
                dialog_tombol_ok.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(Shell.SU.available()) {
                            (new StartUp()).setContext().execute("sysui");
                        } else {
                            munculkanDialogBelumRootUntuk(toggle_restartui);
                        }
                    }
                });
                dialog_tombol_cancel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        toggle_restartui.setChecked(false);
                        dialog_view.dismiss();
                    }
                });
                dialog_view.show();
            }
        });

        /****************************************************************/
        /* 9. mobile data */
        toggle_data.setOnClickListener(new ToggleButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = pref_data.edit();
                editor.putBoolean("tgdata", toggle_data.isChecked());
                editor.apply();
            }
        });
        toggle_data.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Shell.SU.available()) {
                    if (isChecked) {
                        (new StartUp()).setContext().execute("on_data");
                    } else {
                        (new StartUp()).setContext().execute("off_data");
                    }
                } else {
                    munculkanDialogBelumRootUntuk(toggle_data);
                }
            }
        });
        pref_data = mContext.getSharedPreferences("tgdata", Context.MODE_PRIVATE);
        boolean tgdata = pref_data.getBoolean("tgdata", false);//default false
        toggle_data.setChecked(tgdata);

        /****************************************************************/
        /* 10. WiFi */
        toggle_wifi.setOnClickListener(new ToggleButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = pref_wifi.edit();
                editor.putBoolean("tgwifi", toggle_wifi.isChecked());
                editor.apply();
            }
        });
        toggle_wifi.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (Shell.SU.available()) {
                    if (isChecked) {
                        (new StartUp()).setContext().execute("on_wifi");
                    } else {
                        (new StartUp()).setContext().execute("off_wifi");
                    }
                } else {
                    munculkanDialogBelumRootUntuk(toggle_wifi);
                }
            }
        });
        pref_wifi = mContext.getSharedPreferences("tgwifi", Context.MODE_PRIVATE);
        boolean tgwifi = pref_wifi.getBoolean("tgwifi", false);//default false
        toggle_wifi.setChecked(tgwifi);

        /****************************************************************/
        /* 11. Bluetooth */
        toggle_bluetooth.setOnClickListener(new ToggleButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = pref_bluetooth.edit();
                editor.putBoolean("tgbluetooth", toggle_bluetooth.isChecked());
                editor.apply();
            }
        });
        toggle_bluetooth.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(Shell.SU.available()) {
                    if (isChecked) {
                        (new StartUp()).setContext().execute("on_blutut");
                    } else {
                        (new StartUp()).setContext().execute("off_blutut");
                    }
                } else {
                    munculkanDialogBelumRootUntuk(toggle_bluetooth);
                }
            }
        });
        pref_bluetooth = mContext.getSharedPreferences("tgbluetooth", Context.MODE_PRIVATE);
        boolean tgbluetooth = pref_bluetooth.getBoolean("tgbluetooth", false);//default false
        toggle_bluetooth.setChecked(tgbluetooth);

        /****************************************************************/
        /* 12. Mode Pesawat / Airplane */
        toggle_pesawat.setOnClickListener(new ToggleButton.OnClickListener() {
            @Override
            public void onClick(View v) {
                SharedPreferences.Editor editor = pref_pesawat.edit();
                editor.putBoolean("tgpesawat", toggle_pesawat.isChecked());
                editor.apply();
            }
        });
        toggle_pesawat.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(Shell.SU.available()) {
                    if (isChecked) {
                        (new StartUp()).setContext().execute("on_pesawat");
                    } else {
                        (new StartUp()).setContext().execute("off_pesawat");
                    }
                } else {
                    munculkanDialogBelumRootUntuk(toggle_pesawat);
                }
            }
        });
        pref_pesawat = mContext.getSharedPreferences("tgpesawat", Context.MODE_PRIVATE);
        boolean tgpesawat = pref_pesawat.getBoolean("tgpesawat", false);//default false
        toggle_pesawat.setChecked(tgpesawat);
    }

    /****************************************************************
     * AsyncTask
     ****************************************************************/
    private class StartUp extends AsyncTask<String,Void,Void> {
        public StartUp setContext() {
            return this;
        }
        @Override
        protected Void doInBackground(String... params) {
                /* Kumpulan script Shell su
                 * Tes manualnya di Terminal Emulator apk
                 */
                switch (params[0]){
                    case "reboot"  : Shell.SU.run("reboot");break;
                    case "soft_reboot"  : Shell.SU.run("setprop ctl.restart zygote");break;
                    case "safe_mode"  : Shell.SU.run(REBOOT_SAFE_MODE);break;
                    case "recovery"  : Shell.SU.run("reboot recovery");break;
                    case "bootloader"  : Shell.SU.run("reboot bootloader");break;
                    case "modar"  : Shell.SU.run("reboot -p");break;
                    case "sysui"   : Shell.SU.run("pkill com.android.systemui");break;
                    case "on_data"  : Shell.SU.run("svc data enable");break;
                    case "off_data"  : Shell.SU.run("svc data disable");break;
                    case "on_wifi"  : Shell.SU.run("svc wifi enable");break;
                    case "off_wifi"  : Shell.SU.run("svc wifi disable");break;
                    case "on_blutut"    : Shell.SU.run("service call bluetooth_manager 6");break;
                    case "off_blutut"   : Shell.SU.run("service call bluetooth_manager 8");break;
                    case "on_pesawat"   : Shell.SU.run(MODE_PESAWAT_NYALA);break;
                    case "off_pesawat"   : Shell.SU.run(MODE_PESAWAT_MATI);break;
                }
            return null;
        }
    }

    /* membuka browser untuk akses URL
     * https://mykodetutorial.blogspot.com
     */
    private void pergiKeURL (String url) {
        Uri uriUrl = Uri.parse(url);
        Intent launchBrowser = new Intent(Intent.ACTION_VIEW, uriUrl);
        mContext.startActivity(launchBrowser);
    }

    /* mengganti id menjadi string */
    private int setResource(String nama, String tipe){
        return getContext().getResources().getIdentifier(nama,tipe,getContext().getPackageName());
    }

    private void munculkanDialogBelumRootUntuk(ToggleButton toggleButton){
        //jika HH belum root
        Toast.makeText(mContext, BELUM_ROOT, Toast.LENGTH_LONG).show();
        dialog_view.dismiss();
        toggleButton.setChecked(false);
    }
    
}

/* Author    : suyonoion
 * Blog      : tutorial ada di https://mykodetutorial.blogspot.com
 * FB        : https://fb.com/suyono.ion
 * GitHub    : https://github.com/suyonoion
 * Google+   : https://plus.google.com/105560119529420031036
 * */