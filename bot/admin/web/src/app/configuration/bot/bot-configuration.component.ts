/*
 * Copyright (C) 2017 VSCT
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import {Component, OnInit} from "@angular/core";
import {BotConfigurationService} from "../../core/bot-configuration.service";
import {BotApplicationConfiguration, ConnectorType, UserInterfaceType} from "../../core/model/configuration";
import {MdDialog, MdSnackBar} from "@angular/material";
import {ConfirmDialogComponent} from "tock-nlp-admin/src/app/shared/confirm-dialog/confirm-dialog.component";
import {StateService} from "tock-nlp-admin/src/app/core/state.service";

@Component({
  selector: 'tock-bot-configuration',
  templateUrl: './bot-configuration.component.html',
  styleUrls: ['./bot-configuration.component.css']
})
export class BotConfigurationComponent implements OnInit {

  newApplicationConfiguration: BotApplicationConfiguration;

  constructor(private state: StateService,
              public botConfiguration: BotConfigurationService,
              private snackBar: MdSnackBar,
              private dialog: MdDialog) {
  }

  ngOnInit() {
  }

  prepareCreate() {
    this.newApplicationConfiguration = new BotApplicationConfiguration(
      this.state.currentApplication.name,
      this.state.currentApplication.name,
      this.state.currentApplication.namespace,
      this.state.currentApplication.name,
      new ConnectorType("messenger", UserInterfaceType.textChat, true),
      this.state.currentApplication.name);
  }

  cancelCreate() {
    this.newApplicationConfiguration = null;
  }

  create() {
    this.botConfiguration.saveConfiguration(this.newApplicationConfiguration)
      .subscribe(_ => {
        this.botConfiguration.updateConfigurations();
        this.snackBar.open(`Configuration created`, "Creation", {duration: 5000});
      });
    this.newApplicationConfiguration = null;
  }

  update(conf: BotApplicationConfiguration) {
    this.botConfiguration.saveConfiguration(conf)
      .subscribe(_ => {
        this.botConfiguration.updateConfigurations();
        this.snackBar.open(`Configuration updated`, "Update", {duration: 5000});
      });
  }

  delete(conf: BotApplicationConfiguration) {
    let dialogRef = this.dialog.open(ConfirmDialogComponent, {
      data: {
        title: `Delete the configuration`,
        subtitle: "Are you sure?",
        action: "Remove"
      }
    });
    dialogRef.afterClosed().subscribe(result => {
      if (result === "remove") {
        this.botConfiguration.deleteConfiguration(conf)
          .subscribe(_ => {
            this.botConfiguration.updateConfigurations();
            this.snackBar.open(`Configuration deleted`, "Delete", {duration: 5000});
          });
      }
    });


  }

}