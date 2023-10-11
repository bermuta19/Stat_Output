/*
 * Copyright (c) 2018, Adam <Adam@sigterm.info>
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT OWNER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.stat_output;

import java.net.*;
import java.io.*;

import java.util.Collection;

import lombok.extern.slf4j.Slf4j;
import net.runelite.client.game.ItemManager;
import net.runelite.client.game.ItemStack;

import javax.inject.Inject;
import java.util.LinkedList;
import net.runelite.api.Client;
import net.runelite.api.Skill;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;


//express js, javascript
//fastapi, flask, django python
//LootTrackerPlugin.java line 627
// Stat_Output.AddCollection(items);
//import StatOutput plugin into LootTrackerPlugin
//LootTrackerPlugin line 1449 set toGameItem to public
//for intellij set this directory
//C:\Users\nolan\IdeaProjects\runelite3\runelite-client\src\main\resources\net\runelite\client\runelite.properties
//change runelite.pluginhub.version to runelite.pluginhub.version=1.10.14
//add external JAR file of jython to intelli idea project
@Slf4j
@PluginDescriptor(
		name = "Stat output",
		description = "Output health and stats"
)
public class StatOutputPlugin extends Plugin
{


	private static int Health;
	private static int Prayer;
	private static int Strength;
	private static String nameMonster;
	private static LinkedList<ItemStack> item_drops_formatted;
	private static LinkedList<Loot> loot = new LinkedList<Loot>();
	private int HealthPrevious = -1;
	private int PrayerPrevious = -1;
	private int PottedPrevious = -1;
	private int ItemPrevious1 = -1;
	private int ItemPrevious2 = -1;
	private int ItemPrevious3 = -1;
	private int Potted = -1;

	@Inject
	private Client client;
	@Inject
	private ItemManager itemManager;

	public static void postValuesNumber(String address,int value){
		String temp = "{"+"\"" +address.toLowerCase() +"\": " + value + "}";

		try {
			// TODO look at the okhttp client instead of trying to do it like this
			// TODO you haven't got a hostname here
			String request = "http://" +"/" + address;
			URL url = new URL(request);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setDoOutput(true);
			OutputStream os = con.getOutputStream();
			byte[] input = temp.getBytes("utf-8");
			os.write(input,0,input.length);



			BufferedReader br = new BufferedReader(
					new InputStreamReader(con.getInputStream(), "utf-8"));
			StringBuilder response = new StringBuilder();
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}

			//System.out.println(response.toString());
		}catch (Exception ignore){}
	}

	public static void postValuesItem(String address,String Element1, int value1,String Element2, int value2,String Element3, int value3){
		// TODO use gson to build json strings
		String idTemp1 = "\"" + Element1 + "\": " + value1 +",";
		String idTemp2 = "\"" + Element2 + "\": " + value2 +",";
		String idTemp3 = "\"" + Element3 + "\": " + value3;

		try {
			String temp = "{" + idTemp1 + idTemp2 + idTemp3 + "}";
			String request = "" +"/" + address;
			URL url = new URL(request);
			HttpURLConnection con = (HttpURLConnection) url.openConnection();
			con.setRequestMethod("POST");
			con.setRequestProperty("Content-Type", "application/json");
			con.setRequestProperty("Accept", "application/json");
			con.setDoOutput(true);
			OutputStream os = con.getOutputStream();
			byte[] input = temp.getBytes("utf-8");
			os.write(input,0,input.length);



			BufferedReader br = new BufferedReader(
					new InputStreamReader(con.getInputStream(), "utf-8"));
			StringBuilder response = new StringBuilder();
			String responseLine = null;
			while ((responseLine = br.readLine()) != null) {
				response.append(responseLine.trim());
			}

			//System.out.println(response.toString());
		}catch (Exception ignore){}
	}





	public void AddCollection(String name, Collection<ItemStack> item)
	{
		nameMonster = name;
		item_drops_formatted = new LinkedList<ItemStack>(item);
		this.makeLoot(item_drops_formatted);

	}
	private void makeLoot(LinkedList<ItemStack> item_drops_formatted){

		for(int i = 0;i<item_drops_formatted.size();i++) {
			Loot lootTemp = new Loot(item_drops_formatted.get(i).getId(), item_drops_formatted.get(i).getQuantity());
			lootTemp.setPrice(itemManager.getItemPrice(lootTemp.getId()));
			if (lootTemp.getPrice()>100){
				loot.addFirst(lootTemp);
				if (loot.size() > 3) {
					loot.removeLast();
				}
			}
		}
	}


	@Subscribe
	public void onGameTick(GameTick event)
	{	Health = client.getBoostedSkillLevel(Skill.PRAYER);
		Prayer = client.getBoostedSkillLevel(Skill.HITPOINTS);
		Strength = client.getBoostedSkillLevel(Skill.STRENGTH);


		if(Strength <118)
		{
			Potted = -1;
		} else {
			Potted = 0;
		}
		if(Health != HealthPrevious){
			postValuesNumber("Health",Health);


		}
		if(Prayer != PrayerPrevious){
			postValuesNumber("Prayer",Prayer);

		}
		if(Potted != PottedPrevious){
			postValuesNumber("Potted",Potted);

		}

		if(loot.size() > 0){

			if(loot.get(0).getId() != ItemPrevious1){
				postValuesItem("Item1","id",loot.get(0).getId(),"quantity",loot.get(0).getQuantity(),"price",loot.get(0).getPrice());

			}
			if(loot.size() >1){
				if(loot.get(1).getId() != ItemPrevious2){

					postValuesItem("Item2","id",loot.get(1).getId(),"quantity",loot.get(1).getQuantity(),"price",loot.get(1).getPrice());


				}
				if(loot.size() >2){
					if(loot.get(2).getId() != ItemPrevious3){
						postValuesItem("Item3","id",loot.get(2).getId(),"quantity",loot.get(2).getQuantity(),"price",loot.get(2).getPrice());


					}
				}
			}
		}

		if(loot.size() > 0) {
			ItemPrevious1 = loot.get(0).getId();
			if(loot.size() >1){
				ItemPrevious2 = loot.get(1).getId();
				if(loot.size() > 2) {
					ItemPrevious3 = loot.get(2).getId();
				}
			}
		}


		HealthPrevious = Health;
		PrayerPrevious = Prayer;
		Potted = PottedPrevious;


	}




}
