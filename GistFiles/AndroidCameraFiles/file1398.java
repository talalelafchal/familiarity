		try {

				JSONObject reader = new JSONObject(response);

				name = reader.getString("name");
				price = reader.getString("price");
				discount = reader.getString("discount");
				brand = reader.getString("brand");
				qty = reader.getString("qty");
				in_stock = reader.getString("in_stock");
				variation_type = reader.getString("variation_type");
				description = reader.getString("description");
				checkout_buttons = reader.getString("checkout_buttons");
				specifications = reader.getString("specifications");
				seller_info = reader.getString("seller_info");
				image = reader.getString("image");
				other_images = reader.getString("other_images");


				// --------------JSONObject second_level----------------;

				JSONObject second_level = new JSONObject(seller_info);

				shop_id = second_level.getString("shop_id");
				shop_name = second_level.getString("shop_name");
				been_here = second_level.getString("been_here");
				reviews = second_level.getString("reviews");
				products = second_level.getString("products");
				favorites = second_level.getString("favorites");


				// ----------------------------------
				// for getting image
				JSONObject images_level_ob = new JSONObject(image);

				original_single_image = images_level_ob.getString("original");

				Log.d("jan17", original + "");

				// ----------------------------------
				// for getting other images

				JSONObject images_otherimages_ob = new JSONObject(other_images);

				JSONArray images_otherimages_arr = images_otherimages_ob
						.getJSONArray("original");

				Log.d("json17o", images_otherimages_arr + "");

				for (int i = 0; i < images_otherimages_arr.length(); i++) {
					images4productdetail.add(images_otherimages_arr
							.getString(i));
				}

				original = images_level_ob.getString("original");

				Log.d("json2", images4productdetail + "");

				// ----------------------------------------------
				// for fetching specifications
				// first of all i clear the Product_Detail_Desc class main
				// arraylist
				arraylist.clear();

				JSONArray specifications_ob = new JSONArray(specifications);

				for (int i = 0; i < specifications_ob.length(); i++) {

					JSONObject specifications_ob_2 = specifications_ob
							.getJSONObject(i);

					heading_of_product_desc = specifications_ob_2
							.getString("heading");

					heading_of_product_desc_arrlist
							.add(heading_of_product_desc);


					titles_of_product_desc = specifications_ob_2.getString("titles");


					JSONArray titles_of_product_desc_ob = new JSONArray(
							titles_of_product_desc);


					for (int j = 0; j < titles_of_product_desc_ob.length(); j++) {

						JSONObject titles_of_product_desc_json_ob = titles_of_product_desc_ob
								.getJSONObject(j);

						String title_title_of_product_desc = titles_of_product_desc_json_ob
								.getString("title");

						title_of_product_desc_arrlist
								.add(title_title_of_product_desc);

						String value_titles_of_product_desc = titles_of_product_desc_json_ob
								.getString("values");

						value_of_product_desc_arrlist
								.add(value_titles_of_product_desc);

						product_list_hm.put(heading_of_product_desc,
								title_title_of_product_desc);

						// ----------------
						if (j == 0) {
							arraylist.add(new ProductDetailListModel(
											Product_Detail.title_of_product_desc_arrlist
													.get(j),
											Product_Detail.value_of_product_desc_arrlist
													.get(j),
											Product_Detail.heading_of_product_desc_arrlist
													.get(i), (byte) i));
						}


						// ----------------

					}
				}

				// --------------------------------------------

			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				Log.d("5jan", e.toString() + " I catched");
			}
