/*
 * Copyright (C) 2017/2020 e-voyageurs technologies
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */


import {Interaction} from './Interaction';

export class InteractionEntry {
  type: 'bot' | 'user';
  content: InteractionEntryContent[];
  interactions: Interaction[];
}

export class InteractionEntryContent {
  type: 'text' | 'carrousel';
  text: string;
}

export class CarouselCard {
  label: string;
  url: string;
  interaction_id: string;
}
