package com.contestnotifier.backend.fetcher;

import com.contestnotifier.backend.entity.Contest;

import java.util.List;

public interface ContestFetcher {

    List<Contest> fetchContests();
}
