/*
 * Copyright (C) 2023 Fraunhofer Institut IOSB, Fraunhoferstr. 1, D 76131
 * Karlsruhe, Germany.
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
package de.fraunhofer.iosb.ilt.frostclient.models;

import static de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypeComplex.STA_MAP;
import static de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypePrimitive.EDM_DATETIMEOFFSET;
import static de.fraunhofer.iosb.ilt.frostclient.model.property.type.TypePrimitive.EDM_STRING;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsMultiDatastreamV11.NAME_MULTI_DATASTREAM;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsMultiDatastreamV11.NAME_MULTI_DATASTREAMS;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsSensingV11.EP_ID;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsSensingV11.EP_NAME;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsSensingV11.NAME_DATASTREAM;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsSensingV11.NAME_DATASTREAMS;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsSensingV11.NAME_OBSERVATION;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsSensingV11.NAME_OBSERVATIONS;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsSensingV11.NAME_THING;
import static de.fraunhofer.iosb.ilt.frostclient.models.SensorThingsSensingV11.NAME_THINGS;

import de.fraunhofer.iosb.ilt.frostclient.model.Entity;
import de.fraunhofer.iosb.ilt.frostclient.model.EntityType;
import de.fraunhofer.iosb.ilt.frostclient.model.ModelRegistry;
import de.fraunhofer.iosb.ilt.frostclient.model.property.EntityPropertyMain;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntity;
import de.fraunhofer.iosb.ilt.frostclient.model.property.NavigationPropertyEntitySet;
import de.fraunhofer.iosb.ilt.frostclient.models.ext.MapValue;

/**
 *
 */
public class SensorThingsPlus implements DataModel {

    private static final String NAME_OBSERVATION_GROUP = "ObservationGroup";
    private static final String NAME_OBSERVATION_GROUPS = "ObservationGroups";
    private static final String NAME_LICENSE = "License";
    private static final String NAME_LICENSES = "Licenses";
    private static final String NAME_PARTY = "Party";
    private static final String NAME_PARTIES = "Parties";
    private static final String NAME_CAMPAIGN = "Campaign";
    private static final String NAME_CAMPAIGNS = "Campaigns";
    private static final String NAME_RELATION = "Relation";
    private static final String NAME_RELATIONS = "Relations";
    private static final String NAME_SUBJECT = "Subject";
    private static final String NAME_SUBJECTS = "Subjects";
    private static final String NAME_OBJECT = "Object";
    private static final String NAME_OBJECTS = "Objects";

    private static final String NAME_AUTHID = "authId";
    private static final String NAME_CLASSIFICATION = "classifiction";
    private static final String NAME_DISPLAYNAME = "displayName";
    private static final String NAME_ROLE = "role";
    private static final String NAME_PURPOSE = "purpose";
    private static final String NAME_END_TIME = "endTime";
    private static final String NAME_START_TIME = "startTime";
    private static final String NAME_CREATION_TIME = "creationTime";
    private static final String NAME_TERMS_OF_USE = "termsOfUse";
    private static final String NAME_PRIVACY_POLICY = "privacyPolicy";
    private static final String NAME_DATA_QUALITY = "dataQuality";
    private static final String NAME_LOGO = "logo";
    private static final String NAME_ATTRIBUTION_TEXT = "attributionText";
    private static final String NAME_URL = "url";
    private static final String NAME_PROPERTIES = "properties";
    private static final String NAME_EXTERNAL_OBJECT = "externalObject";

    public static final EntityPropertyMain<String> EP_AUTHID = new EntityPropertyMain<>(NAME_AUTHID, EDM_STRING);
    public static final EntityPropertyMain<String> EP_CLASSIFICATION = new EntityPropertyMain<>(NAME_CLASSIFICATION, EDM_STRING);
    public static final EntityPropertyMain<String> EP_DEFINITION = SensorThingsSensingV11.EP_DEFINITION;
    public static final EntityPropertyMain<String> EP_DESCRIPTION = SensorThingsSensingV11.EP_DESCRIPTION;
    public static final EntityPropertyMain<String> EP_DISPLAYNAME = new EntityPropertyMain<>(NAME_DISPLAYNAME, EDM_STRING);
    public static final EntityPropertyMain<String> EP_PURPOSE = new EntityPropertyMain<>(NAME_PURPOSE, EDM_STRING);
    public static final EntityPropertyMain<String> EP_ROLE = new EntityPropertyMain<>(NAME_ROLE, EDM_STRING);
    public static final EntityPropertyMain<String> EP_START_TIME = new EntityPropertyMain<>(NAME_START_TIME, EDM_DATETIMEOFFSET);
    public static final EntityPropertyMain<String> EP_END_TIME = new EntityPropertyMain<>(NAME_END_TIME, EDM_DATETIMEOFFSET);
    public static final EntityPropertyMain<String> EP_CREATION_TIME = new EntityPropertyMain<>(NAME_CREATION_TIME, EDM_DATETIMEOFFSET);
    public static final EntityPropertyMain<String> EP_TERMS_OF_USE = new EntityPropertyMain<>(NAME_TERMS_OF_USE, EDM_STRING);
    public static final EntityPropertyMain<String> EP_PRIVACY_POLICY = new EntityPropertyMain<>(NAME_PRIVACY_POLICY, EDM_STRING);
    public static final EntityPropertyMain<MapValue> EP_DATA_QUALITY = new EntityPropertyMain<>(NAME_DATA_QUALITY, STA_MAP);
    public static final EntityPropertyMain<MapValue> EP_PROPERTIES = SensorThingsSensingV11.EP_PROPERTIES;
    public static final EntityPropertyMain<String> EP_LOGO = new EntityPropertyMain<>(NAME_LOGO, EDM_STRING);
    public static final EntityPropertyMain<String> EP_ATTRIBUTION_TEXT = new EntityPropertyMain<>(NAME_ATTRIBUTION_TEXT, EDM_STRING);
    public static final EntityPropertyMain<String> EP_URL = new EntityPropertyMain<>(NAME_URL, EDM_STRING);
    public static final EntityPropertyMain<String> EP_EXTERNAL_OBJECT = new EntityPropertyMain<>(NAME_EXTERNAL_OBJECT, EDM_STRING);

    public final NavigationPropertyEntity npCampaignParty = new NavigationPropertyEntity(NAME_PARTY);
    public final NavigationPropertyEntitySet npPartyCampaigns = new NavigationPropertyEntitySet(NAME_CAMPAIGNS, npCampaignParty);

    public final NavigationPropertyEntity npGroupParty = new NavigationPropertyEntity(NAME_PARTY);
    public final NavigationPropertyEntitySet npPartyGroups = new NavigationPropertyEntitySet(NAME_OBSERVATION_GROUPS, npGroupParty);

    public final NavigationPropertyEntity npDatastreamParty = new NavigationPropertyEntity(NAME_PARTY);
    public final NavigationPropertyEntitySet npPartyDatastreams = new NavigationPropertyEntitySet(NAME_DATASTREAMS, npDatastreamParty);

    public final NavigationPropertyEntity npMultiDatastreamParty = new NavigationPropertyEntity(NAME_PARTY);
    public final NavigationPropertyEntitySet npPartyMultiDatastreams = new NavigationPropertyEntitySet(NAME_MULTI_DATASTREAMS, npMultiDatastreamParty);

    public final NavigationPropertyEntity npThingParty = new NavigationPropertyEntity(NAME_PARTY);
    public final NavigationPropertyEntitySet npPartyThings = new NavigationPropertyEntitySet(NAME_THINGS, npThingParty);

    public final NavigationPropertyEntity npCampaignLicense = new NavigationPropertyEntity(NAME_LICENSE);
    public final NavigationPropertyEntitySet npLicenseCampaigns = new NavigationPropertyEntitySet(NAME_CAMPAIGNS, npCampaignLicense);

    public final NavigationPropertyEntity npGroupLicense = new NavigationPropertyEntity(NAME_LICENSE);
    public final NavigationPropertyEntitySet npLicenseGroups = new NavigationPropertyEntitySet(NAME_OBSERVATION_GROUPS, npGroupLicense);

    public final NavigationPropertyEntity npDatastreamLicense = new NavigationPropertyEntity(NAME_LICENSE);
    public final NavigationPropertyEntitySet npLicenseDatastreams = new NavigationPropertyEntitySet(NAME_DATASTREAMS, npDatastreamLicense);

    public final NavigationPropertyEntity npMultiDatastreamLicense = new NavigationPropertyEntity(NAME_LICENSE);
    public final NavigationPropertyEntitySet npLicenseMultiDatastreams = new NavigationPropertyEntitySet(NAME_MULTI_DATASTREAMS, npMultiDatastreamLicense);

    public final NavigationPropertyEntity npRelationSubject = new NavigationPropertyEntity(NAME_SUBJECT);
    public final NavigationPropertyEntitySet npObservationObjects = new NavigationPropertyEntitySet(NAME_OBJECTS, npRelationSubject);

    public final NavigationPropertyEntity npRelationObject = new NavigationPropertyEntity(NAME_OBJECT);
    public final NavigationPropertyEntitySet npObservationSubjects = new NavigationPropertyEntitySet(NAME_SUBJECTS, npRelationObject);

    public final NavigationPropertyEntitySet npRelationGroups = new NavigationPropertyEntitySet(NAME_OBSERVATION_GROUPS);
    public final NavigationPropertyEntitySet npGroupRelations = new NavigationPropertyEntitySet(NAME_RELATIONS, npRelationGroups);

    public final NavigationPropertyEntitySet npCampaignGroups = new NavigationPropertyEntitySet(NAME_OBSERVATION_GROUPS);
    public final NavigationPropertyEntitySet npGroupCampaigns = new NavigationPropertyEntitySet(NAME_CAMPAIGNS, npCampaignGroups);

    public final NavigationPropertyEntity npObservationGroups = new NavigationPropertyEntity(NAME_OBSERVATION_GROUPS);
    public final NavigationPropertyEntitySet npGroupObservations = new NavigationPropertyEntitySet(NAME_OBSERVATIONS, npObservationGroups);

    public final NavigationPropertyEntitySet npDatastreamCampaigns = new NavigationPropertyEntitySet(NAME_CAMPAIGNS);
    public final NavigationPropertyEntitySet npCampaignDatastreams = new NavigationPropertyEntitySet(NAME_DATASTREAMS, npDatastreamCampaigns);

    public final NavigationPropertyEntitySet npMultiDatastreamCampaigns = new NavigationPropertyEntitySet(NAME_CAMPAIGNS);
    public final NavigationPropertyEntitySet npCampaignMultiDatastreams = new NavigationPropertyEntitySet(NAME_MULTI_DATASTREAMS, npMultiDatastreamCampaigns);

    public final EntityType etGroup = new EntityType(NAME_OBSERVATION_GROUP, NAME_OBSERVATION_GROUPS);
    public final EntityType etLicense = new EntityType(NAME_LICENSE, NAME_LICENSES);
    public final EntityType etParty = new EntityType(NAME_PARTY, NAME_PARTIES);
    public final EntityType etCampaign = new EntityType(NAME_CAMPAIGN, NAME_CAMPAIGNS);
    public final EntityType etRelation = new EntityType(NAME_RELATION, NAME_RELATIONS);

    private ModelRegistry mr;

    public SensorThingsPlus() {
    }

    @Override
    public final void init(ModelRegistry modelRegistry) {
        if (this.mr != null) {
            throw new IllegalArgumentException("Already initialised.");
        }
        this.mr = modelRegistry;
        mr.addDataModel(this);

        final EntityType etDatastream = mr.getEntityTypeForName(NAME_DATASTREAM);
        final EntityType etThing = mr.getEntityTypeForName(NAME_THING);
        final EntityType etObservation = mr.getEntityTypeForName(NAME_OBSERVATION);
        final EntityType etMultiDatastream = mr.getEntityTypeForName(NAME_MULTI_DATASTREAM);

        mr.registerEntityType(etGroup);
        mr.registerEntityType(etLicense);
        mr.registerEntityType(etParty);
        mr.registerEntityType(etCampaign);
        mr.registerEntityType(etRelation);

        etGroup
                .registerProperty(EP_ID)
                .registerProperty(ModelRegistry.EP_SELFLINK)
                .registerProperty(EP_NAME)
                .registerProperty(EP_DESCRIPTION)
                .registerProperty(EP_PURPOSE)
                .registerProperty(EP_END_TIME)
                .registerProperty(EP_CREATION_TIME)
                .registerProperty(EP_TERMS_OF_USE)
                .registerProperty(EP_PRIVACY_POLICY)
                .registerProperty(EP_PROPERTIES)
                .registerProperty(EP_DATA_QUALITY)
                .registerProperty(npGroupLicense)
                .registerProperty(npGroupObservations)
                .registerProperty(npGroupParty)
                .registerProperty(npGroupCampaigns)
                .registerProperty(npGroupRelations);

        etLicense
                .registerProperty(EP_ID)
                .registerProperty(ModelRegistry.EP_SELFLINK)
                .registerProperty(EP_NAME)
                .registerProperty(EP_DESCRIPTION)
                .registerProperty(EP_DEFINITION)
                .registerProperty(EP_LOGO)
                .registerProperty(EP_ATTRIBUTION_TEXT)
                .registerProperty(npLicenseCampaigns)
                .registerProperty(npLicenseGroups)
                .registerProperty(npLicenseDatastreams)
                .registerProperty(npLicenseMultiDatastreams);

        etParty
                .registerProperty(EP_ID)
                .registerProperty(ModelRegistry.EP_SELFLINK)
                .registerProperty(EP_DESCRIPTION)
                .registerProperty(EP_AUTHID)
                .registerProperty(EP_ROLE)
                .registerProperty(EP_DISPLAYNAME)
                .registerProperty(npPartyCampaigns)
                .registerProperty(npPartyGroups)
                .registerProperty(npPartyThings)
                .registerProperty(npPartyDatastreams)
                .registerProperty(npPartyMultiDatastreams);

        etCampaign
                .registerProperty(EP_ID)
                .registerProperty(ModelRegistry.EP_SELFLINK)
                .registerProperty(EP_NAME)
                .registerProperty(EP_DESCRIPTION)
                .registerProperty(EP_CLASSIFICATION)
                .registerProperty(EP_TERMS_OF_USE)
                .registerProperty(EP_PRIVACY_POLICY)
                .registerProperty(EP_CREATION_TIME)
                .registerProperty(EP_START_TIME)
                .registerProperty(EP_END_TIME)
                .registerProperty(EP_URL)
                .registerProperty(EP_PROPERTIES)
                .registerProperty(npCampaignParty)
                .registerProperty(npCampaignLicense)
                .registerProperty(npCampaignDatastreams)
                .registerProperty(npCampaignMultiDatastreams)
                .registerProperty(npCampaignGroups);

        etRelation
                .registerProperty(EP_ID)
                .registerProperty(ModelRegistry.EP_SELFLINK)
                .registerProperty(EP_ROLE)
                .registerProperty(EP_DESCRIPTION)
                .registerProperty(EP_EXTERNAL_OBJECT)
                .registerProperty(EP_PROPERTIES)
                .registerProperty(npRelationGroups)
                .registerProperty(npRelationSubject)
                .registerProperty(npRelationObject);

        etObservation
                .registerProperty(npObservationGroups)
                .registerProperty(npObservationSubjects)
                .registerProperty(npObservationObjects);

        etThing
                .registerProperty(npThingParty);

        etDatastream
                .registerProperty(npDatastreamLicense)
                .registerProperty(npDatastreamParty)
                .registerProperty(npDatastreamCampaigns);

        // Register entities on multiDatastream, if it exists.
        if (etMultiDatastream != null) {
            etMultiDatastream
                    .registerProperty(npMultiDatastreamLicense)
                    .registerProperty(npMultiDatastreamParty)
                    .registerProperty(npMultiDatastreamCampaigns);
        }
    }

    @Override
    public boolean isInitialised() {
        return mr != null;
    }

    public ModelRegistry getModelRegistry() {
        return mr;
    }

    public Entity newGroup() {
        return new Entity(etGroup);
    }

    public Entity newGroup(Object id) {
        return newGroup()
                .setPrimaryKeyValues(id);
    }

    public Entity newGroup(String name, String description) {
        return newGroup()
                .setProperty(EP_NAME, name)
                .setProperty(EP_DESCRIPTION, description);
    }

    public Entity newLicense() {
        return new Entity(etLicense);
    }

    public Entity newLicense(Object id) {
        return newLicense()
                .setPrimaryKeyValues(id);
    }

    public Entity newLicense(String name, String description, String definition) {
        return newLicense()
                .setProperty(EP_NAME, name)
                .setProperty(EP_DESCRIPTION, description)
                .setProperty(EP_DEFINITION, definition);
    }

    public Entity newParty() {
        return new Entity(etGroup);
    }

    public Entity newParty(String authId) {
        return newParty()
                .setPrimaryKeyValues(authId);
    }

    public Entity newParty(String authId, String displayName, String description) {
        return newParty(authId)
                .setProperty(EP_DISPLAYNAME, displayName)
                .setProperty(EP_DESCRIPTION, description);
    }

    public Entity newParty(String authId, String displayName, String description, String role) {
        return newParty(authId, displayName, description)
                .setProperty(EP_ROLE, role);
    }

    public Entity newProject() {
        return new Entity(etCampaign);
    }

    public Entity newProject(Object id) {
        return newProject()
                .setPrimaryKeyValues(id);
    }

    public Entity newProject(String name, String description) {
        return newProject()
                .setProperty(EP_NAME, name)
                .setProperty(EP_DESCRIPTION, description);
    }

    public Entity newRelation() {
        return new Entity(etRelation);
    }

    public Entity newRelation(Object id) {
        return newRelation()
                .setPrimaryKeyValues(id);
    }

    public Entity newRelation(String role, String description) {
        return newRelation()
                .setProperty(EP_ROLE, role)
                .setProperty(EP_DESCRIPTION, description);
    }

}
