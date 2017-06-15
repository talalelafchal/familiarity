 @Test
    public void findMostBusyVariants() {
        pushMessageInformation = pushMessageInformationDao.find(pushMessageInformationID);

        final String loginName = "admin";

        VariantMetricInformation variantOne = new VariantMetricInformation();
        variantOne.setDeliveryStatus(Boolean.FALSE);
        variantOne.setReceivers(200);
        variantOne.setVariantID("231543432432");
        pushMessageInformation.getVariantInformations().add(variantOne);
        pushMessageInformationDao.update(pushMessageInformation);

        VariantMetricInformation variantThree = new VariantMetricInformation();
        variantThree.setDeliveryStatus(Boolean.FALSE);
        variantThree.setReceivers(300);
        variantThree.setVariantID("23154343243333");
        pushMessageInformation.getVariantInformations().add(variantThree);
        pushMessageInformationDao.update(pushMessageInformation);



        VariantMetricInformation variantFour = new VariantMetricInformation();
        variantFour.setDeliveryStatus(Boolean.FALSE);
        variantFour.setReceivers(1000);
        variantFour.setVariantID("231543432434");
        pushMessageInformation.getVariantInformations().add(variantFour);
        pushMessageInformationDao.update(pushMessageInformation);

        VariantMetricInformation variantFive = new VariantMetricInformation();
        variantFive.setDeliveryStatus(Boolean.TRUE);
        variantFive.setReceivers(50000);
        variantFive.setVariantID("231543432434");
        pushMessageInformation.getVariantInformations().add(variantFive);
        pushMessageInformationDao.update(pushMessageInformation);


        PushMessageInformation pmi = new PushMessageInformation();
        pmi.setPushApplicationId("231231231");
        pushMessageInformationDao.create(pmi);
        VariantMetricInformation variantTwo = new VariantMetricInformation();
        variantTwo.setDeliveryStatus(Boolean.TRUE);
        variantTwo.setReceivers(2000);
        variantTwo.setVariantID("231543432432");
        pmi.getVariantInformations().add(variantTwo);
        pushMessageInformationDao.update(pmi);


        final AndroidVariant androidVariant = new AndroidVariant();
        androidVariant.setGoogleKey("123");
        androidVariant.setVariantID("231543432432");
        androidVariant.setDeveloper(loginName);
        entityManager.persist(androidVariant);

        final AndroidVariant androidVariant1 = new AndroidVariant();
        androidVariant1.setGoogleKey("123");
        androidVariant1.setVariantID("23154343243333");
        androidVariant1.setDeveloper(loginName);
        entityManager.persist(androidVariant1);

        final AndroidVariant androidVariant2 = new AndroidVariant();
        androidVariant2.setGoogleKey("123");
        androidVariant2.setVariantID("231543432434");
        androidVariant2.setDeveloper(loginName);
        entityManager.persist(androidVariant2);

        flushAndClear();

        Map<String, Long> busyVariants = pushMessageInformationDao.findTopThreeBusyVariantIDs(loginName);
        assertThat(busyVariants).hasSize(3);
        assertThat(busyVariants.keySet())
                .contains("231543432432", "23154343243333", "231543432434");
    }